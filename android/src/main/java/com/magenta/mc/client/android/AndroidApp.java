package com.magenta.mc.client.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.android.setup.AndroidSetup;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.util.AndroidResourceManager;
import com.magenta.mc.client.android.mc.client.ConnectionListener;
import com.magenta.mc.client.android.mc.client.Login;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.util.ResourceManager;

public class AndroidApp extends MobileApp {

    protected Context applicationContext;

    private volatile int needToLoginRequestsCount;

    public AndroidApp(String[] args, Context applicationContext) {
        super(args);
        this.applicationContext = applicationContext;
    }

    protected void afterSetInstance() {
        run();
    }

    protected void run() {
        super.run();
        McAndroidApplication.resetSettingsUserId();
        initUncaughtExceptionHandler();
        needToLogin();
    }

    public void needToLogin() {
        MCLoggerFactory.getLogger(getClass()).debug("incrementing needToLoginRequestsCount, old value  " + needToLoginRequestsCount);
        needToLoginRequestsCount++;
        if (needToLoginRequestsCount == 2) {
            needToLoginRequestsCount = 0;
            try {
                String pinAndPass = Setup.get().getSettings().getUserIdAndPassword();
                final String[] pass = pinAndPass.split(";");
                if (pass.length == 2) {
                    MobileApp.runTask(new Runnable() {
                        public void run() {
                            Login.getInstance().doLogin(pass[0], pass[1], new Runnable() {
                                public void run() {
                                    MCLoggerFactory.getLogger(getClass()).debug("log in completed after service restart");
                                }
                            });
                        }
                    });
                } else {
                    MCLoggerFactory.getLogger(getClass()).error(String.format("Pin [%s] is wrong.", pinAndPass));
                }
            } catch (Exception e) {
                MCLoggerFactory.getLogger(getClass()).error("Cant login after service restart", e);
            }
        }
    }

    protected void initUncaughtExceptionHandler() {
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, final Throwable throwable) {
                MCLoggerFactory.getLogger(Thread.UncaughtExceptionHandler.class).error(throwable.getMessage(), throwable);
                defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
            }
        });
    }

    protected void initResourceManager() {
        ResourceManager.init(new AndroidResourceManager(applicationContext));
    }

    protected void initSetup() {
        Setup.init(new AndroidSetup(applicationContext));
    }

    protected void setupConnectionListener() {
        super.setupConnectionListener();
        final ConnectionListener.Listener listener = ConnectionListener.getInstance().getListener();
        ConnectionListener.getInstance().setListener(new ConnectionListener.Listener() {
            public void connected() {
                listener.connected();
                connectionStateChanged(true);
            }

            public void disconnected() {
                listener.disconnected();
                connectionStateChanged(false);
            }
        });
    }

    private void connectionStateChanged(final boolean online) {
        runTask(new Runnable() {
            public void run() {
                NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = ((AndroidUI) Setup.get().getUI()).getNotifications().createConnectionStatusChangeNotification(online);
                if (notification != null) {
                    notificationManager.notify(McService.MAIN_NOTIFICATION_ID, notification);
                }
            }
        });
    }
}