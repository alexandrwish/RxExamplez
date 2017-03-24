package com.magenta.mc.client.android.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.android.setup.Setup;

public class Notifications {

    private static final String TAG_CONNECTION_STATE = "mc-connection-state";

    private Context context;
    private boolean lastNotificationIsAlert;

    public Notifications(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    private boolean isLastNotificationIsAlert() {
        return lastNotificationIsAlert;
    }

    protected void setLastNotificationIsAlert(boolean lastNotificationIsAlert) {
        this.lastNotificationIsAlert = lastNotificationIsAlert;
    }

    public Notification createConnectionStatusChangeNotification(boolean online) {
        if (!isLastNotificationIsAlert()) {
            return createConnectionStatusNotification(online, null);
        }
        return null;
    }

    public Notification createGPSNotification() {
        return null;
    }

    public Notification createConnectionStatusNotification(boolean online, String applicationName) {
        final ApplicationIcons icons = ((AndroidUI) Setup.get().getUI()).getApplicationIcons();
        setLastNotificationIsAlert(false);
        return new NotificationBuilder(context)
                .setId(TAG_CONNECTION_STATE, McService.MAIN_NOTIFICATION_ID)
                .setIcon(online ? icons.getOnline() : icons.getOffline())
                .setTitle(applicationName == null ? getContext().getString(R.string.mx_app_name) : applicationName)
                .setMessage(context.getString(online ? R.string.mc_notification_status_online : R.string.mc_notification_status_offline))
                .create();
    }

    public static final class NotificationBuilder {

        private final Context context;
        private String message;
        private String title;
        private String tag;
        private int icon;
        private int id;

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
            return new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()), 0))
                    .build();
        }

        public void show(Notification notification) {
            if (notification == null) {
                notification = create();
            }
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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