package com.magenta.mc.client.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.listener.BroadcastEvent;
import com.magenta.mc.client.android.listener.BroadcastEventsListener;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.setup.Setup;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.Notifications;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CoreServiceGeneric extends Service implements CoreService {

    private static LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    protected final Map<String, BroadcastEventsListener> broadcastListeners = new ConcurrentHashMap<>(0);
    private final IBinder mBinder = new LocalBinder();

    public void registerListener(final BroadcastEventsListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (broadcastListeners) {
            broadcastListeners.put(listener.getId(), listener);
        }
    }

    public void removeListener(final BroadcastEventsListener listener) {
        if (listener == null) {
            return;
        }
        removeListener(listener.getId());
    }

    public void removeListener(final String id) {
        synchronized (broadcastListeners) {
            broadcastListeners.remove(id);
        }
    }

    @SuppressWarnings("unchecked")
    public void notifyListeners(final BroadcastEvent event) {
        synchronized (broadcastListeners) {
            for (final BroadcastEventsListener listener : broadcastListeners.values()) {
                if (listener.getFilter().contains(event.getType())) {
                    try {
                        listener.onEvent(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MCLoggerFactory.getLogger(CoreServiceGeneric.class).error(e);
                    }
                }
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, locationListener);
        MCLoggerFactory.getLogger(getClass()).trace("starting in foreground");
        Notifications notifications = ((AndroidUI) Setup.get().getUI()).getNotifications();
        String appName = getString(R.string.mx_app_name);
        startForeground(McService.MAIN_NOTIFICATION_ID, notifications.createConnectionStatusNotification(false, appName));
        MCLoggerFactory.getLogger(getClass()).trace("started in foreground");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MCLoggerFactory.getLogger(CoreServiceGeneric.class).info("LocalService: Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public CoreServiceGeneric getService() {
            return CoreServiceGeneric.this;
        }
    }
}