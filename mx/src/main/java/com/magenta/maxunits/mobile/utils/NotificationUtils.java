package com.magenta.maxunits.mobile.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;

/**
 * @author Sergey Grachev
 */
public class NotificationUtils {
    private static final int NOTIFICATION_ID = NotificationUtils.class.hashCode();

    private static NotificationManager getNotificationManager(final Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void audioAlarm(final Context context) {
        final Notification notification = new Notification();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.sound = Uri.parse("android.resource://"
                + context.getApplicationContext().getPackageName() + "/raw/incoming_update");
        getNotificationManager(context).notify(NOTIFICATION_ID, notification);
    }
}
