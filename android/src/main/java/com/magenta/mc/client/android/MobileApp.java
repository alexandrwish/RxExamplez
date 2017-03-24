package com.magenta.mc.client.android;

import com.magenta.mc.client.android.components.MCTimerTask;
import com.magenta.mc.client.android.demo.DemoStorageInitializer;
import com.magenta.mc.client.android.entity.AbstractJobStatus;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.resender.ResendableMetadata;
import com.magenta.mc.client.android.resender.Resender;
import com.magenta.mc.client.android.service.SaveLocationsService;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.setup.MxSetup;
import com.magenta.mc.client.android.setup.Setup;
import com.magenta.mc.client.android.storage.DemoStorageInitializerImpl;
import com.magenta.mc.client.android.tracking.GeoLocationBatch;
import com.magenta.mc.client.android.util.AndroidResourceManager;
import com.magenta.mc.client.android.util.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.Timer;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;
import jaba.util.PropertyResourceBundleUtf8;

public class MobileApp {

    protected static MobileApp instance;
    // a thread pool with unbounded queue and limited number of threads
    // should be used for every asynchronous task
    private static PooledExecutor mainThreadPool;
    // a thread pool with unbounded queue and single thread
    // designed for showing Dialogs one by one
    private static PooledExecutor consecutiveExecutor;
    private static ResourceBundle resourceBundle;
    private final DemoStorageInitializer demoStorageInitializer;
    private Timer timer;
    private long prevCheckMillis = System.currentTimeMillis();

    public MobileApp() {
        demoStorageInitializer = new DemoStorageInitializerImpl();
        instance = this;
        run();
    }

    public static MobileApp getInstance() {
        return instance;
    }

    public static void setInstance(MobileApp instance) {
        MobileApp.instance = instance;
        MobileApp.instance.afterSetInstance();
    }

    public static void runTask(Runnable task) {
        runOnThreadPool(task, mainThreadPool);
    }

    public static void runConsecutiveTask(Runnable task) {
        runOnThreadPool(task, consecutiveExecutor);
    }

    private static void runOnThreadPool(Runnable task, PooledExecutor pool) {
        try {
            pool.execute(task);
        } catch (InterruptedException e) {
            // pool interrupted, probably system shutdown
        }
    }

    public static String localize(String key) {
        if (resourceBundle == null) {
            //final String locale = Setup.get().getSettings().getProperty("i18n.locale", "en");
            String locale = Locale.getDefault().getLanguage();
            String messagesFilename = "messages_" + locale + ".properties"; // defaults to messages_en.properties
            InputStream stream = ResourceManager.getInstance().getResourceAsStream(messagesFilename);//Thread.currentThread().getContextClassLoader().getResourceAsStream(messagesFilename);
            if (stream == null) {
                locale = "en"; // try english locale as default
                messagesFilename = "messages_" + locale + ".properties"; // defaults to messages_en.properties
                stream = ResourceManager.getInstance().getResourceAsStream(messagesFilename);//Thread.currentThread().getContextClassLoader().getResourceAsStream(messagesFilename);
                if (stream == null) {
                    throw new RuntimeException("Resource bundle " + messagesFilename + " not found");
                }
            }
            try {
                resourceBundle = new PropertyResourceBundleUtf8(stream);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create resource bundle from " + messagesFilename, e);
            }
        }
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return "!not localized: [" + key + "]";
        }
    }

    private void afterSetInstance() {
        run();
    }

    public Timer getTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        return timer;
    }

    private void stopThreadPools() {
        mainThreadPool.shutdownNow();
        consecutiveExecutor.shutdownNow();
    }

    protected void run() {
        initResourceManager();

        initSetup();

        initLogger();

        setupTimeZone();

        setupLocale();

        setupThreadPools();

        setupTimeChecking();

        setupMemoryUsageLogging();

        setupGeoLocationAPI();

        Resender.getInstance().registerResendables(getResendablesMetadata());

        McAndroidApplication.resetSettingsUserId();
    }

    private void initSetup() {
        Setup.init(new MxSetup(McAndroidApplication.getInstance(), demoStorageInitializer));
    }

    private ResendableMetadata[] getResendablesMetadata() {
        ResendableMetadata[] res = new ResendableMetadata[]{GeoLocationBatch.METADATA};
        return joinMetadata(res, new ResendableMetadata[]{AbstractJobStatus.RESENDABLE_METADATA});
    }

    private ResendableMetadata[] joinMetadata(ResendableMetadata[] meta1, ResendableMetadata[] meta2) {
        ResendableMetadata[] result = new ResendableMetadata[meta1.length + meta2.length];
        System.arraycopy(meta1, 0, result, 0, meta1.length);
        System.arraycopy(meta2, 0, result, meta1.length, meta2.length);
        return result;
    }

    private void setupGeoLocationAPI() {
        ServicesRegistry.startSaveLocationsService(SaveLocationsService.class);
    }

    private void stopGeoLocationAPI() {
        ServicesRegistry.stopSaveLocationsService();
    }

    private void setupTimeChecking() {
        getTimer().schedule(
                new MCTimerTask() {
                    public void runTask() {
                        long now = System.currentTimeMillis();
                        long delta = Math.abs(now - prevCheckMillis - 1000 * 30);
                        if (delta > 1000 * 60) { //one minute
                            MCLoggerFactory.getLogger("TimeChecking").warn("Time was changed: " + delta / 1000.0 / 60 / 60
                                    + "h calc new time delta");
                        }
                        prevCheckMillis = now;
                    }
                }, 1000 * 30, 1000 * 30);     //check time every 30 sec
    }

    private void setupMemoryUsageLogging() {
        getTimer().schedule(
                new MCTimerTask() {
                    public void runTask() {
                        MCLoggerFactory.getLogger("MemoryUsage").debug("Free " + Runtime.getRuntime().totalMemory() / 1024
                                + " KiB, Total " + Runtime.getRuntime().totalMemory() / 1024 + " KiB, Max "
                                + Runtime.getRuntime().maxMemory() / 1024 + " KiB, " + Thread.activeCount() + " threads");
                    }
                }, 0, 1000 * 60 * 10);     //eat memory usage every 10 min
    }

    private void initResourceManager() {
        ResourceManager.init(new AndroidResourceManager(McAndroidApplication.getInstance()));
    }

    protected void init() {
        // override and put initialization logic
    }

    private void setupTimeZone() {
        String strTimezone = com.magenta.mc.client.android.common.Settings.get().getTimezone();
        if (!"DEFAULT".equalsIgnoreCase(strTimezone)) {
            TimeZone timezone = null;
            String[] avails = TimeZone.getAvailableIDs();
            for (String avail : avails) {
                if (avail.equals(strTimezone)) {
                    timezone = TimeZone.getTimeZone(strTimezone);
                    break;
                }
            }
            if (timezone != null) {
                TimeZone.setDefault(timezone);
                MCLoggerFactory.getLogger(MobileApp.class).info("using timezone " + TimeZone.getDefault().getID());
            } else {
                MCLoggerFactory.getLogger(MobileApp.class).warn("wrong timezone " + strTimezone + ", use default timezone");
            }
        }
    }

    private void setupLocale() {
        String localeKey = com.magenta.mc.client.android.common.Settings.get().getLocale();
        if (!"DEFAULT".equalsIgnoreCase(localeKey)) {
            String language;
            String region;
            String country = "";
            String variant = "";
            // example: en_GB_
            int i = localeKey.indexOf('_');
            if (i >= 0) {
                language = localeKey.substring(0, i);
                region = localeKey.substring(i + 1);
                if (region.length() > 0) {
                    // region can be of form country, country_variant, or _variant
                    int k = region.indexOf('_');
                    if (k >= 0) {
                        country = region.substring(0, k);
                        variant = region.substring(k + 1);
                    } else {
                        country = region;
                    }
                }
            } else {
                language = localeKey;
            }
            Locale customLocale = new Locale(language, country, variant);
            Locale.setDefault(customLocale);
        }
    }

    private void initLogger() {
        MCLoggerFactory.getInstance().initLogging();
    }

    private void setupThreadPools() {
        // default size is 3 = (N + 1) + 1
        // given N = 1 - number of processors, + 1 thread for servicing I/O of XMPP stream
        final int poolSize = /*Setup.get().getSettings().getIntProperty("main-thread-pool.size", "3")*/3;
        mainThreadPool = new PooledExecutor(new LinkedQueue(), poolSize);
        mainThreadPool.setMinimumPoolSize(poolSize);
        mainThreadPool.waitWhenBlocked();
        mainThreadPool.setThreadFactory(new ThreadFactory() {
            private int threadNum = 1;

            public Thread newThread(Runnable command) {
                return new Thread(command, "Main-" + threadNum++);
            }
        });
        mainThreadPool.setKeepAliveTime(-1);
        consecutiveExecutor = new PooledExecutor(new LinkedQueue(), 1);
        consecutiveExecutor.setThreadFactory(new ThreadFactory() {
            private int threadNum = 1;

            public Thread newThread(Runnable command) {
                return new Thread(command, "Consecutive-" + threadNum++);
            }
        });
        consecutiveExecutor.setKeepAliveTime(-1);
    }

    protected void stop() {
        stopGeoLocationAPI();
        stopThreadPools();
        Setup.get().getUI().shutdown();
    }

    public void exit() {
        stop();
        System.exit(0);
    }
}