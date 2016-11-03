package com.magenta.mc.client.android.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

/**
 * @author Sergey Grachev
 */
public class Notifications {
    private static final String TAG_CONNECTION_STATE = "mc-connection-state";

    private Context context;
    private boolean lastNotificationIsAlert;

    public Notifications(Context context) {
        this.context = context;
    }

    public static void showConnectionStatus(final Context context, final boolean online, final String applicationName) {
        Notification connectionStatusNotification =
                ((AndroidUI) Setup.get().getUI()).getNotifications().createConnectionStatusNotification(online, applicationName);
        new NotificationBuilder(context).show(connectionStatusNotification);
    }

    protected Context getContext() {
        return context;
    }

    public boolean isLastNotificationIsAlert() {
        return lastNotificationIsAlert;
    }

    public void setLastNotificationIsAlert(boolean lastNotificationIsAlert) {
        this.lastNotificationIsAlert = lastNotificationIsAlert;
    }

    public Notification createConnectionStatusChangeNotification(boolean online) {
        if (!isLastNotificationIsAlert()) {
            return createConnectionStatusNotification(online, null);
        }
        return null;
    }

    public Notification createConnectionStatusNotification(boolean online, String applicationName) {
        final ApplicationIcons icons = ((AndroidUI) Setup.get().getUI()).getApplicationIcons();
        setLastNotificationIsAlert(false);
        return new NotificationBuilder(context)
                .setId(TAG_CONNECTION_STATE, McService.MAIN_NOTIFICATION_ID)
                .setIcon(online ? icons.getOnline() : icons.getOffline())
                .setTitle(applicationName == null ? Settings.get().getAppName() : applicationName)
                .setMessage(context.getString(online
                        ? R.string.mc_notification_status_online : R.string.mc_notification_status_offline))
                .create();
    }

    public Notification createGPSNotification() {
        Notification notification = new Notification(
                ((AndroidUI) Setup.get().getUI()).getApplicationIcons().getAlert(),
                context.getString(R.string.mc_commons_notification_gpsDisabled), System.currentTimeMillis());
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        Activity currentActivity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        PendingIntent contentIntent = PendingIntent.getActivity(currentActivity, 0, gpsOptionsIntent, 0);
        notification.setLatestEventInfo(currentActivity,
                context.getString(R.string.mc_commons_notification_gpsDisabled),
                context.getString(R.string.mc_commons_notification_gpsOpenSettings), contentIntent);
        setLastNotificationIsAlert(true);
        return notification;
    }

    public static final class NotificationBuilder {
        private final Context context;
        private String title;
        private String message;
        private int icon;
        private int id;
        private String tag;

        public NotificationBuilder(final Context context) {
            this.context = context;
        }

        public NotificationBuilder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder setIcon(final int icon) {
            this.icon = icon;
            return this;
        }

        public NotificationBuilder setId(final int id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder setId(final String tag, final int id) {
            this.id = id;
            this.tag = tag;
            return this;
        }

        public Notification create() {
            return new Notification(icon, title, System.currentTimeMillis());
        }

        public void show(Notification notification) {
            if (notification == null) {
                notification = create();
            }
            final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()), 0);
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.setLatestEventInfo(context, title, message, contentIntent);
            if (tag != null) {
                notificationManager.notify(tag, id, notification);
            } else {
                notificationManager.notify(id, notification);
            }
        }

        public void show() {
            show(null);
        }
    }
}
