package com.magenta.mc.client.android;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
import android.os.PowerManager;
import android.view.ContextThemeWrapper;

import com.google.inject.Module;
import com.magenta.mc.client.android.acra.AcraConfigurator;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.common.UserStatus;
import com.magenta.mc.client.android.db.DBAdapter;
import com.magenta.mc.client.android.db.MxDBOpenHelper;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.db.dao.TileCacheDAO;
import com.magenta.mc.client.android.mc.MXNavApp;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.CoreServiceImpl;
import com.magenta.mc.client.android.service.LocationService;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.DataControllerImpl;
import com.magenta.mc.client.android.ui.activity.SettingsActivity;
import com.magenta.mc.client.android.ui.theme.Theme;
import com.magenta.mc.client.android.ui.theme.ThemeManageable;
import com.magenta.mc.client.android.ui.theme.ThemeManager;
import com.magenta.mc.client.android.util.DSoundPool;
import com.magenta.mc.client.android.util.LocaleUtils;
import com.magenta.mc.client.android.util.StringUtils;
import com.magenta.mc.client.android.util.WorkflowServiceImpl;

import org.acra.annotation.ReportsCrashes;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import roboguice.RoboGuice;

@ReportsCrashes(formKey = "")
public abstract class McAndroidApplication extends Application implements ThemeManageable, ThemeManager.ThemeManagerListener {

    protected static boolean isFirstStart;
    protected static McAndroidApplication instance;
    protected ThemeManager themeManager;
    protected DBAdapter dbAdapter;
    protected MXNavApp mxNavApp;
    protected boolean locked = false;
    protected PowerManager.WakeLock lock;
    private UserStatus status;

    public static McAndroidApplication getInstance() {
        return instance;
    }

    //Override this method for clear Settings when App was started;
    public static void resetSettingsUserId() {
        if (isFirstStart) {
            isFirstStart = false;
            //Clear UserID
        }
    }

    public abstract String getName();

    public void onCreate() {
        MxDBOpenHelper.setDatabase("maxunits_distribution");
        super.onCreate();
        MCLoggerFactory.getLogger(getClass()).trace("onCreate");
        isFirstStart = true;
        setupGuice();
        setupThemeManager();
        instance = this;
        initNavAppClient();
        initDBAdapter();
        initAcra();
//        Renderer.registerRenderers(SingleJobRenderer.class);
        ServicesRegistry.registerDataController(new DataControllerImpl());
        ServicesRegistry.registerWorkflowService(WorkflowServiceImpl.class);
        ServicesRegistry.startCoreService(this, CoreServiceImpl.class);
        ServicesRegistry.startLocationService(this, LocationService.class);
        new MobileApp();
        LocaleUtils.changeLocale(this, Settings.get().getLocale());
        DSoundPool.init(getInstance());
        try {
            TileCacheDAO.getInstance().removeCacheTiles(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)); // TODO: 3/12/17 impl
        } catch (SQLException ignore) {
        }
        lock();
        MCLoggerFactory.getLogger(this.getClass()).info(getDeviceName());
    }

    private String capitalize(String s) {
        if (StringUtils.isBlank(s)) return "";
        char first = s.charAt(0);
        return Character.isUpperCase(first) ? s : (Character.toUpperCase(first) + s.substring(1));
    }

    private void setupThemeManager() {
        themeManager = createThemeManager();
        themeManager.setListener(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.changeLocale(this, Settings.get().getLocale());
    }

    public void onTerminate() {
        MCLoggerFactory.getLogger(getClass()).trace("onTerminate");
        MobileApp.getInstance().exit();
        if (mxNavApp != null) {
            this.mxNavApp.unregisterCurrentLocationListener();
            this.mxNavApp.destroy();
            this.mxNavApp = null;
        }
        ServicesRegistry.stopCoreService();
        super.onTerminate();
    }

    protected void initDBAdapter() {
        this.dbAdapter = new DBAdapter(this);
    }

    private void initNavAppClient() {
        try {
            this.mxNavApp = MXNavApp.init(this);
            this.mxNavApp.registerCurrentLocationListener();
        } catch (Exception ignore) {
            this.mxNavApp = null;
        }
    }

    public DBAdapter getDBAdapter() {
        return dbAdapter;
    }

    public MXNavApp getMxNavApp() {
        return mxNavApp;
    }

    public ThemeManager createThemeManager() {
        return new MxThemeManager();
    }

    /**
     * Override this method if You want hide settings of your app.
     *
     * @return set of settings keys
     */
    public Set<String> getHiddenSettings() {
        Set<String> result = new HashSet<>();
        result.add("ui.theme");
        return result;
    }

    private void setupGuice() {
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(this), createModule());
    }

    protected Module createModule() {
        return new McModule();
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public void onChangeTheme() {
        setTheme(themeManager.getCurrentThemeId(null));
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    protected void initAcra() {
        AcraConfigurator acraConfigurator = new AcraConfigurator();
        acraConfigurator.init(this);
    }

    public void completeStatisticSending(Date date) {
        try {
            DistributionDAO.getInstance().clearStatistics(date);
        } catch (SQLException e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }

    public void lock() {
        if (locked) return;
        if (lock == null) {
            lock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "distribution_lock");
        }
        lock.acquire();
        locked = true;
    }

    public void unlock() {
        if (locked && lock != null) {
            lock.release();
            locked = false;
        }
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    private static class MxThemeManager extends ThemeManager {

        protected int getThemeId(ContextThemeWrapper context, Theme theme) {
            if (context != null && context instanceof SettingsActivity) {
                switch (theme) {
                    case DAY:
                        return R.style.McTheme_NoTitleBar_Light;
                    case NIGHT:
                        return R.style.McTheme_NoTitleBar;
                    default:
                        throw new IllegalArgumentException();
                }
            } else {
                switch (theme) {
                    case DAY:
                        return R.style.McTheme_Light;
                    case NIGHT:
                        return R.style.McTheme;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
    }
}