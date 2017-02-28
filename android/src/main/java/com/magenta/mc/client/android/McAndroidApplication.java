package com.magenta.mc.client.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;

import com.google.inject.Module;
import com.magenta.mc.client.android.db.MxDBAdapter;
import com.magenta.mc.client.android.mc.MXNavApp;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.android.ui.activity.common.SettingsActivity;
import com.magenta.mc.client.android.ui.theme.Theme;
import com.magenta.mc.client.android.ui.theme.ThemeManageable;
import com.magenta.mc.client.android.ui.theme.ThemeManager;
import com.magenta.mc.client.android.util.LocaleUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import roboguice.RoboGuice;

public abstract class McAndroidApplication extends Application implements ThemeManageable, ThemeManager.ThemeManagerListener {

    protected static boolean isFirstStart;
    protected static McAndroidApplication instance;
    protected ThemeManager themeManager;
    protected MxDBAdapter dbAdapter;
    protected MXNavApp mxNavApp;
    protected boolean loginPress;
    private Timer mTimer;

    public static McAndroidApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance != null ? instance.getApplicationContext() : null;
    }

    //Override this method for clear Settings when App was started;
    public static void resetSettingsUserId() {
        if (isFirstStart) {
            isFirstStart = false;
            //Clear UserID
            Settings settings = Setup.get().getSettings();
            settings.setUserId("");
            settings.saveSettings();
        }
    }

    public void onCreate() {
        super.onCreate();
        MCLoggerFactory.getLogger(getClass()).trace("onCreate");
        isFirstStart = true;
        setupGuice();
        startMcService();
        setupThemeManager();
        instance = this;
        initNavAppClient();
        initDBAdapter();
    }

    private void setupThemeManager() {
        themeManager = createThemeManager();
        themeManager.setListener(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.changeLocale(this, MxSettings.get().getProperty(Settings.LOCALE_KEY));
    }

    public void completeStatisticSending(Date date) {
    }

    public void checkHistory(Runnable runnable) {
    }

    public boolean isLoginPress() {
        return loginPress;
    }

    public void setLoginPress(boolean loginPress) {
        this.loginPress = loginPress;
    }

    public void onTerminate() {
        MCLoggerFactory.getLogger(getClass()).trace("onTerminate");
        AndroidApp.getInstance().exit();
        if (mxNavApp != null) {
            this.mxNavApp.unregisterCurrentLocationListener();
            this.mxNavApp.destroy();
            this.mxNavApp = null;
        }
        super.onTerminate();
    }


    protected void initDBAdapter() {
        this.dbAdapter = new MxDBAdapter(this);
    }

    private void initNavAppClient() {
        try {
            this.mxNavApp = MXNavApp.init(this);
            this.mxNavApp.registerCurrentLocationListener();
        } catch (Exception ignore) {
            this.mxNavApp = null;
        }
    }

    public MxDBAdapter getDBAdapter() {
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

    protected void startMcService() {
        MCLoggerFactory.getLogger(getClass()).trace("starting service");
        Intent intent = new Intent(this, getServiceClass());
        intent.putExtra("dont_login", true);
        startService(intent);
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public void onChangeTheme() {
        setTheme(themeManager.getCurrentThemeId(null));
    }

    protected abstract Class<? extends McService> getServiceClass();

    public Timer getTimer() {
        return mTimer == null ? mTimer = new Timer(getString(R.string.mx_app_name)) : mTimer;
    }

    private static class MxThemeManager extends ThemeManager {

        protected int getThemeId(ContextThemeWrapper context, Theme theme) {
            if (context != null && context instanceof SettingsActivity) {
                switch (theme) {
                    case day:
                        return R.style.McTheme_NoTitleBar_Light;
                    case night:
                        return R.style.McTheme_NoTitleBar;
                    default:
                        throw new IllegalArgumentException();
                }
            } else {
                switch (theme) {
                    case day:
                        return R.style.McTheme_Light;
                    case night:
                        return R.style.McTheme;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
    }
}