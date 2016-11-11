package com.magenta.maxunits.mobile.dlib;

import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.magenta.maxunits.mobile.MxApplication;
import com.magenta.maxunits.mobile.db.MxDBOpenHelper;
import com.magenta.maxunits.mobile.dlib.acra.AcraConfigurator;
import com.magenta.maxunits.mobile.dlib.db.DBAdapter;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.dlib.db.dao.TileCacheDAO;
import com.magenta.maxunits.mobile.dlib.mc.DistributionApp;
import com.magenta.maxunits.mobile.dlib.receiver.LoginCheckReceiver;
import com.magenta.maxunits.mobile.dlib.service.CoreServiceImpl;
import com.magenta.maxunits.mobile.dlib.service.PhoneStatisticService;
import com.magenta.maxunits.mobile.dlib.service.renderer.JobHistoryRenderer;
import com.magenta.maxunits.mobile.dlib.service.renderer.SingleJobRenderer;
import com.magenta.maxunits.mobile.dlib.service.storage.DataControllerImpl;
import com.magenta.maxunits.mobile.dlib.utils.DSoundPool;
import com.magenta.maxunits.mobile.dlib.utils.WorkflowServiceImpl;
import com.magenta.maxunits.mobile.dlib.xmpp.XMPPStream2;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.renderer.Renderer;
import com.magenta.maxunits.mobile.service.LocationService;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.utils.LocaleUtils;
import com.magenta.maxunits.mobile.utils.StringUtils;
import com.magenta.mc.client.log.MCLoggerFactory;

import org.acra.annotation.ReportsCrashes;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;

//import org.apache.http.client.HttpClient;

@ReportsCrashes(formKey = "")
public abstract class DistributionApplication extends MxApplication {

    protected boolean locked = false;
    protected PowerManager.WakeLock lock;

    public void onCreate() {
        MxDBOpenHelper.setDatabase("maxunits_distribution");
        super.onCreate();
        initAcra();
        Renderer.registerRenderers(SingleJobRenderer.class, JobHistoryRenderer.class);
        ServicesRegistry.registerDataController(new DataControllerImpl());
        ServicesRegistry.registerWorkflowService(WorkflowServiceImpl.class);
        ServicesRegistry.startCoreService(this, CoreServiceImpl.class);
        ServicesRegistry.startLocationService(this, LocationService.class);
        new DistributionApp();
        startService(new Intent(this, PhoneStatisticService.class));
        LocaleUtils.changeLocale(this, MxSettings.getInstance().getLocale());
        DSoundPool.init(getContext());
        try {
            TileCacheDAO.getInstance(this).removeCacheTiles(System.currentTimeMillis() - (MxSettings.getInstance().getIntProperty(MxSettings.CLEAN_CACHE_PERIOD, "0") * 24 * 60 * 60 * 1000));
        } catch (SQLException ignore) {
        }
        lock();
        MCLoggerFactory.getLogger(this.getClass()).info(getDeviceName());
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
            DistributionDAO.getInstance(getContext()).clearStatistics(date);
        } catch (SQLException e) {
            MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }

    public void checkHistory(final Runnable runnable) {
        DistributionApp.runConsecutiveTask(new Runnable() {
            public void run() {
                OkHttpClient client = XMPPStream2.getThreadSafeClient();
                List<String> drivers = DistributionDAO.getInstance(DistributionApplication.getContext()).getDrivers();
                final LoginCheckReceiver receiver = new LoginCheckReceiver();
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