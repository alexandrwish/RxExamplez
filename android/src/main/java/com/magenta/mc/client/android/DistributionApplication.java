package com.magenta.mc.client.android;

import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.magenta.mc.client.android.acra.AcraConfigurator;
import com.magenta.mc.client.android.db.DBAdapter;
import com.magenta.mc.client.android.db.MxDBOpenHelper;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.db.dao.TileCacheDAO;
import com.magenta.mc.client.android.mc.DistributionApp;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.renderer.Renderer;
import com.magenta.mc.client.android.rpc.xmpp.XMPPStream2;
import com.magenta.mc.client.android.service.CoreServiceImpl;
import com.magenta.mc.client.android.service.LocationService;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.android.service.PhoneStatisticService;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.renderer.SingleJobRenderer;
import com.magenta.mc.client.android.service.storage.DataControllerImpl;
import com.magenta.mc.client.android.task.LoginCheckTask;
import com.magenta.mc.client.android.util.DSoundPool;
import com.magenta.mc.client.android.util.LocaleUtils;
import com.magenta.mc.client.android.util.StringUtils;
import com.magenta.mc.client.android.util.WorkflowServiceImpl;

import org.acra.annotation.ReportsCrashes;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;

@ReportsCrashes(formKey = "")
public abstract class DistributionApplication extends McAndroidApplication {

    protected boolean locked = false;
    protected PowerManager.WakeLock lock;

    public void onCreate() {
        MxDBOpenHelper.setDatabase("maxunits_distribution");
        super.onCreate();
        initAcra();
        Renderer.registerRenderers(SingleJobRenderer.class);
        ServicesRegistry.registerDataController(new DataControllerImpl());
        ServicesRegistry.registerWorkflowService(WorkflowServiceImpl.class);
        ServicesRegistry.startCoreService(this, CoreServiceImpl.class);
        ServicesRegistry.startLocationService(this, LocationService.class);
        new DistributionApp();
        startService(new Intent(this, PhoneStatisticService.class));
        LocaleUtils.changeLocale(this, MxSettings.getInstance().getLocale());
        DSoundPool.init(getContext());
        try {
            TileCacheDAO.getInstance().removeCacheTiles(System.currentTimeMillis() - (MxSettings.getInstance().getIntProperty(MxSettings.CLEAN_CACHE_PERIOD, "0") * 24 * 60 * 60 * 1000));
        } catch (SQLException ignore) {
        }
        lock();
        MCLoggerFactory.getLogger(this.getClass()).info(getDeviceName());
    }


    protected void startMcService() {
        //don't start mc service here
    }

    protected Class<? extends McService> getServiceClass() {
        return null;
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

    public abstract String getName();

    private String capitalize(String s) {
        if (StringUtils.isBlank(s)) return "";
        char first = s.charAt(0);
        return Character.isUpperCase(first) ? s : (Character.toUpperCase(first) + s.substring(1));
    }

    protected void initDBAdapter() {
        dbAdapter = new DBAdapter(this);
    }

    public void onTerminate() {
        ServicesRegistry.stopCoreService();
        super.onTerminate();
    }

    public void completeStatisticSending(Date date) {
        try {
            DistributionDAO.getInstance().clearStatistics(date);
        } catch (SQLException e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }

    public void checkHistory(final Runnable runnable) {
        DistributionApp.runConsecutiveTask(new Runnable() {
            public void run() {
                OkHttpClient client = XMPPStream2.getThreadSafeClient();
                List<String> drivers = DistributionDAO.getInstance().getDrivers();
                final LoginCheckTask receiver = new LoginCheckTask();
                for (String driver : drivers) {
                    receiver.checkDriverAndSentLocations(client, driver, false);
                }
                runnable.run();
            }
        });
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
}