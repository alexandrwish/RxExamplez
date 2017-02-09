package com.magenta.mc.client.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.android.AndroidApp;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.Notifications;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;

import roboguice.service.RoboService;

public class McService extends RoboService {

    public static final int MAIN_NOTIFICATION_ID = 433442;
    public static final String WAKE_LOCK_KEY = "MobileCentralPartialLock";
    private final IBinder binder = new LocalBinder();
    private PowerManager.WakeLock partialLock = null;

    public McService() {
        MCLoggerFactory.getLogger(getClass()).trace("Instantiating");
    }

    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        partialLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_KEY);
        if (!partialLock.isHeld()) {
            partialLock.acquire();
        }
        MCLoggerFactory.getLogger(getClass()).info("onCreate");
        AndroidApp.setInstance(createMobileApp());
        MCLoggerFactory.getLogger(getClass()).trace("starting in foreground");
        Notifications notifications = ((AndroidUI) Setup.get().getUI()).getNotifications();
        String appName = Setup.get().getSettings().getAppName();
        startForeground(MAIN_NOTIFICATION_ID, notifications.createConnectionStatusNotification(false, appName));
        MCLoggerFactory.getLogger(getClass()).trace("started in foreground");
    }

    protected AndroidApp createMobileApp() {
        return new AndroidApp(new String[]{}, this);
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        MCLoggerFactory.getLogger(getClass()).info("onStart");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MCLoggerFactory.getLogger(getClass()).trace("onStartCommand: Received start id " + startId + ": " + intent);
        try {
            if (intent == null || !intent.getBooleanExtra("dont_login", false)) {
                ((AndroidApp) MobileApp.getInstance()).needToLogin();
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error("Error while service onStartCommand", e);
        }
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        MCLoggerFactory.getLogger(getClass()).info("onBind");
        return binder;
    }

    public void onDestroy() {
        MCLoggerFactory.getLogger(getClass()).info("onDestroy");
        if (partialLock.isHeld()) {
            partialLock.release();
        }
        super.onDestroy();
    }

    public boolean onUnbind(Intent intent) {
        MCLoggerFactory.getLogger(getClass()).trace("onUnbind");
        return super.onUnbind(intent);
    }

    public void onLowMemory() {
        MCLoggerFactory.getLogger(getClass()).info("onLowMemory");
        super.onLowMemory();
    }

    public class LocalBinder extends Binder {
        public McService getService() {
            return McService.this;
        }
    }
}