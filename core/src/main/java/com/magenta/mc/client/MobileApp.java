package com.magenta.mc.client;

import com.magenta.mc.client.bin_chunks.BinaryChunkResendable;
import com.magenta.mc.client.bin_chunks.random.RandomBinTransTask;
import com.magenta.mc.client.client.ConnectionListener;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.client.Msg;
import com.magenta.mc.client.client.TimeSynchronization;
import com.magenta.mc.client.client.XMPPClient;
import com.magenta.mc.client.client.resend.ResendableMetadata;
import com.magenta.mc.client.client.resend.Resender;
import com.magenta.mc.client.components.AbortableTask;
import com.magenta.mc.client.components.MCTimerTask;
import com.magenta.mc.client.exception.OperationTimeoutException;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.log_sending.LogRequestProcessor;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.storage.StorableMetadata;
import com.magenta.mc.client.tracking.GeoLocationBatch;
import com.magenta.mc.client.tracking.GeoLocationService;
import com.magenta.mc.client.tracking.GeoLocationServiceConfig;
import com.magenta.mc.client.util.ResourceManager;
import com.magenta.mc.client.xmpp.XMPPStream;
import com.magenta.mc.client.xmpp.extensions.rpc.DefaultRPCQueryListener;
import com.magenta.mc.client.xmpp.extensions.rpc.DefaultRpcResponseHandler;
import com.magenta.mc.client.xmpp.extensions.rpc.JabberRPC;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
    private static boolean runningInTestMode;
    private static boolean started;
    // a thread pool with unbounded queue and limited number of threads
    // should be used for every asynchronous task
    private static PooledExecutor mainThreadPool;
    // a thread pool with unbounded queue and single thread
    // designed for showing Dialogs one by one
    private static PooledExecutor consecutiveExecutor;
    private static int syncTimeoutTaskThreadNum = 1;
    private static ResourceBundle resourceBundle;
    protected String[] startupArgs;
    protected boolean diagnosticRestart = false;
    private Timer timer;
    private long prevCheckMillis = System.currentTimeMillis();
    private Locale customLocale;

    public MobileApp(String[] args) {
        this.startupArgs = args;
    }

    public static void runInTestMode() {
        runningInTestMode = true;
    }

    public static boolean isStarted() {
        return started;
    }

    public static void main(String[] args) {
        instance = new MobileApp(args);
        instance.run();
    }

    public static boolean isRunningInTestMode() {
        return runningInTestMode;
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

    public static void runAbortableTaskWithTimeout(final Runnable task, Runnable abort, long timeout) {
        new AbortableTask(task, abort, timeout).run();
    }

    public static void runSyncTaskWithTimeout(final Runnable task, long timeout) {
        final Object daemonMutex = new Object();
        final List doneContainer = new ArrayList();
        doneContainer.add(Boolean.FALSE);

        final List errorContainer = new ArrayList();

        // given task run asynchronously as a daemon, so it could be killed upon timeout
        final Thread taskDaemon = new Thread(new Runnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    errorContainer.add(e);
                } finally {
                    synchronized (daemonMutex) {
                        if (!((Boolean) doneContainer.get(0)).booleanValue()) {
                            doneContainer.set(0, Boolean.TRUE);
                            daemonMutex.notify();
                        }
                    }
                }
            }
        }, "SyncTimeoutTaskDaemon-" + syncTimeoutTaskThreadNum);
        taskDaemon.setDaemon(true);

        // killer thread runs taskDaemon - so once the killer stops, daemon dies
        final Thread killerThread = new Thread(new Runnable() {
            public void run() {
                taskDaemon.start();
                while (!((Boolean) doneContainer.get(0)).booleanValue()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        taskDaemon.interrupt();
                    }
                }
            }
        }, "SyncTimeoutTaskKiller-" + syncTimeoutTaskThreadNum++);

        try {
            // let the show begin,
            // daemon is gonna die should doneContainer contain TRUE
            killerThread.start();

            synchronized (daemonMutex) {
                if (!((Boolean) doneContainer.get(0)).booleanValue()) {
                    try {
                        daemonMutex.wait(timeout);
                    } catch (InterruptedException e) {
                        // ok
                    }
                }
            }
        } finally { // in finally block to guarantee the killerThread stop
            final boolean done = ((Boolean) doneContainer.get(0)).booleanValue();
            doneContainer.set(0, Boolean.TRUE); // this stops killerThread thus killing taskDaemon
            if (!killerThread.isInterrupted())
                killerThread.interrupt();
            if (!done) {
                throw new OperationTimeoutException("Sync Operation Timeout");
            } else if (errorContainer.size() > 0) {
                final Exception cause = (Exception) errorContainer.get(0);
                throw new RuntimeException(cause.getMessage(), cause);
            }
        }

        // we're done, daemons die, exiting synchronously
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

    public static String localize(String key, String[] params) {
        String localized = MobileApp.localize(key);
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            final String pattern = "{" + i + "}";
            int start;
            while ((start = localized.indexOf(pattern)) > -1) {
                localized = localized.substring(0, start)
                        .concat(param)
                        .concat(localized.substring(start + pattern.length()));
            }
        }
        return localized;
    }

    public XMPPStream initStream(String serverName, String host, int port, boolean ssl, long connectionId) throws IOException {
        return null;
    }

    protected void afterSetInstance() {

    }

    public Timer getTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        return timer;
    }

    protected void startIPC() {

    }

    protected void stopIPC() {

    }

    protected void startDiagnostic() {

    }

    protected void stopDiagnostic() {

    }

    private void stopThreadPools() {
        mainThreadPool.shutdownNow();
        consecutiveExecutor.shutdownNow();
    }

    protected void run() {

        setupIPC();

        startIPC();

        startDiagnostic();

        //it's necessary to set valid ResourceManager before init settings,
        //because ResourceManager using for getting settings file from jar
        initResourceManager();

        // it's important to call this method prior to any other setup
        // to let descendants override settings etc.
        init();

        initSetup();

        initLogger();

        setupTimeZone();

        setupLocale();

        setupThreadPools();

        setupLoginListener();

        setupConnectionListener();

        setupMessageListener();

        setupRPCListeners();

        setupXmppConflictListener();

        setupTimeChecking();

        setupMemoryUsageLogging();

        checkDiagnosticParameters();

        setupGeoLocationAPI();

        Resender.getInstance().registerResendables(getResendablesMetadata());

        afterRun();

        MCLoggerFactory.getLogger(MobileApp.class).info("Application started: " + Setup.get().getSettings().getAppName() +
                " version: " + Setup.get().getSettings().getAppVersion() +
                ", mcc-core: " + Setup.get().getSettings().getMcClientCoreVersion() +
                ", mcc-platform: " + Setup.get().getSettings().getMcClientPlatformVersion());
    }

    protected void afterRun() {

    }

    protected void initSetup() {
        // e.g. Setup.init(new WMSetup());
    }

    protected ResendableMetadata[] getResendablesMetadata() {
        return new ResendableMetadata[]{GeoLocationBatch.METADATA, BinaryChunkResendable.METADATA, RandomBinTransTask.RESENDABLE_METADATA};
    }

    protected StorableMetadata[] joinMetadata(StorableMetadata[] meta1, StorableMetadata[] meta2) {
        StorableMetadata[] result = new StorableMetadata[meta1.length + meta2.length];
        System.arraycopy(meta1, 0, result, 0, meta1.length);
        System.arraycopy(meta2, 0, result, meta1.length, meta2.length);
        return result;
    }

    protected ResendableMetadata[] joinMetadata(ResendableMetadata[] meta1, ResendableMetadata[] meta2) {
        ResendableMetadata[] result = new ResendableMetadata[meta1.length + meta2.length];
        System.arraycopy(meta1, 0, result, 0, meta1.length);
        System.arraycopy(meta2, 0, result, meta1.length, meta2.length);
        return result;
    }

    protected void checkDiagnosticParameters() {

    }

    protected void setupGeoLocationAPI() {
        GeoLocationService.getInstance().init(new GeoLocationServiceConfig(Setup.get().getSettings()));
        GeoLocationService.getInstance().start(false);
    }

    protected void stopGeoLocationAPI() {
        GeoLocationService.getInstance().stop(false);
    }

    protected void handleDiagnosticRestart() {
        // init application data etc.
    }

    protected void setupIPC() {
        //override
    }

    //override
    protected void setupXmppConflictListener() {
        //example
        XMPPClient.getInstance().setXmppConflictListener(new Runnable() {
            public void run() {

            }
        });
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
                            TimeSynchronization.synchronize();
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
                }, 0, 1000 * 60 * 10);     //print memory usage every 10 min
    }

    protected void initResourceManager() {
        ResourceManager.init(ResourceManager.getInstance());
    }

    protected void init() {
        // override and put initialization logic
    }

    protected void setupTimeZone() {
        String strTimezone = Setup.get().getSettings().getProperty(Settings.TIMEZONE_PROPERTY, "DEFAULT");
        if (!"DEFAULT".equalsIgnoreCase(strTimezone)) {
            TimeZone timezone = null;
            String[] avails = TimeZone.getAvailableIDs();
            for (int i = 0; i < avails.length; i++) {
                if (avails[i].equals(strTimezone)) {
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

    public Locale getCustomLocale() {
        return customLocale;
    }

    protected void setupLocale() {
        String localeKey = Setup.get().getSettings().getProperty(Settings.LOCALE_KEY, "DEFAULT");
        if (!"DEFAULT".equalsIgnoreCase(localeKey)) {
            String language = "";
            String region = "";
            String country = "";
            String variant = "";
            // example: en_GB_
            int i = localeKey.indexOf('_');
            if (i >= 0) {
                language = localeKey.substring(0, i);

                region = localeKey.substring(i + 1);

                if (region != null && region.length() > 0) {
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
            customLocale = new Locale(language, country, variant);
            Locale.setDefault(customLocale);
        }
    }

    protected void initLogger() {
        MCLoggerFactory.getInstance().initLogging();
    }

    protected void setupThreadPools() {
        // default size is 3 = (N + 1) + 1
        // given N = 1 - number of processors, + 1 thread for servicing I/O of XMPP stream
        final int poolSize = Setup.get().getSettings().getIntProperty("main-thread-pool.size", "3");
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

    // override this method to set login listener
    protected void setupLoginListener() {
        final Login.Listener loginListener = Login.getInstance().getListener();

        Login.getInstance().setListener(new Login.Listener() {
            public void fail() {
                if (loginListener != null) {
                    loginListener.fail();
                }
            }

            public void successBeforeWake(boolean initiatedByUser) {
                if (loginListener != null) {
                    loginListener.successBeforeWake(initiatedByUser);
                }
                new LogRequestProcessor().checkRequests();
            }

            public void successAfterWake(boolean initiatedByUser) {
                if (loginListener != null) {
                    loginListener.successAfterWake(initiatedByUser);
                }
                started = true;
            }

            public void afterLogout() {
                if (loginListener != null) {
                    loginListener.afterLogout();
                }
                diagnosticRestart = false;
            }
        });
    }

    protected void setupMessageListener() {
        XMPPClient.getInstance().setMsgListener(new XMPPClient.MsgListener() {
            public void incoming(final Msg msg) {
                Setup.get().getUI().getDialogManager().asyncMessageSafe(localize("incoming_message"), msg.getBody());
            }
        });
    }

    // override this method to set connection listener
    protected void setupConnectionListener() {
        final ConnectionListener.Listener listener = ConnectionListener.getInstance().getListener();
        ConnectionListener.getInstance().setListener(new ConnectionListener.Listener() {
            public void connected() {
                listener.connected();
                runTask(new Runnable() {
                    public void run() {
                        Setup.get().getUpdateCheck().check();
                    }
                });
            }

            public void disconnected() {
                listener.disconnected();
            }
        });
    }

    // override this method to set RPC listeners
    protected void setupRPCListeners() {
        JabberRPC.getInstance().setListener(new DefaultRPCQueryListener());
        JabberRPC.getInstance().setHandler(new DefaultRpcResponseHandler());
    }

    protected void stop() {
        stopGeoLocationAPI();
        stopDiagnostic();
        stopIPC();
        stopThreadPools();
        Setup.get().getUI().shutdown();
    }

    public void exit() {
        try {
            stop();
        } finally {
            System.exit(0);
        }
    }
}
