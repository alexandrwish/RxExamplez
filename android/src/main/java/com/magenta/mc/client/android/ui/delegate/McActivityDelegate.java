package com.magenta.mc.client.android.ui.delegate;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.location.LocationManager;
import android.view.MenuItem;

import com.magenta.mc.client.android.log.MCLogger;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.android.setup.Setup;
import com.magenta.mc.client.android.ui.AndroidUI;

import javax.inject.Inject;

public class McActivityDelegate implements ActivityDelegate {

    protected final MCLogger LOG = MCLoggerFactory.getLogger(getClass());
    @Inject
    protected NotificationManager notificationManager;
    @Inject
    protected LocationManager locationManager;
    @Inject
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void onStart() {
        if (Setup.isInitialized()) {
            LOG.trace(getActivity().getLocalClassName() + ": onStart.");
        } else {
            LOG.warn("Setup is not initialized, can't exec onStart actions for " + getActivity().getLocalClassName());
        }
    }

    public void onResume() {
        if (Setup.isInitialized()) {
            LOG.trace(getActivity().getLocalClassName() + ": onResume");
            setUiCurrentActivity();
            setDriverStatus(new Object());
            checkGPS();
        } else {
            LOG.warn("Setup is not initialized, can't exec onResume actions for " + getActivity().getLocalClassName());
        }
    }

    public void onPause() {
        LOG.trace(getActivity().getLocalClassName() + ": onPause.");
    }

    public void onStop() {
        LOG.trace(getActivity().getLocalClassName() + ": onStop.");
    }

    public void onDestroy() {
        LOG.trace(getActivity().getLocalClassName() + ": onDestroy.");
    }

    public void onBackPressed() {
        LOG.trace(getActivity().getLocalClassName() + ": onBackPressed.");
    }

    private void setUiCurrentActivity() {
        try {
            ((AndroidUI) Setup.get().getUI()).switchToActivity(getActivity());
        } catch (Exception e) {
            LOG.warn("Error while switchToActivity: " + getActivity().getLocalClassName());
        }
    }

    public void setDriverStatus(Object o) {
        LOG.trace("Set driver status = " + o.toString());
    }

    private void checkGPS() {
        if (notificationManager != null && locationManager != null) {
            boolean online = /*XMPPClient.getInstance().isLoggedIn()*/true; // TODO: 3/12/17 impl
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Notification notification = ((AndroidUI) Setup.get().getUI()).getNotifications().createGPSNotification();
                if (notification != null) {
                    notificationManager.notify(McService.MAIN_NOTIFICATION_ID, notification);
                }
            } else {
                Notification connectionStatusNotification = ((AndroidUI) Setup.get().getUI()).getNotifications().createConnectionStatusNotification(online, null);
                notificationManager.notify(McService.MAIN_NOTIFICATION_ID, connectionStatusNotification);
            }
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        return onItemSelected(item);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return onItemSelected(item);
    }

    protected boolean onItemSelected(MenuItem item) {
        return true;
    }
}