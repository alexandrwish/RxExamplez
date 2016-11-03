package com.magenta.maxunits.mobile.mc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.magenta.maxunits.mobile.activity.common.LoginActivity;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.ApplicationIcons;
import com.magenta.mc.client.android.ui.Notifications;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

/**
 * @author Sergey Grachev
 */
public class MxNotifications extends Notifications {

    public MxNotifications(final Context context) {
        super(context);
    }

    public static void showConnectionStatus(final Context context, final boolean online, final String applicationName) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = ((AndroidUI) Setup.get().getUI()).getNotifications()
                .createConnectionStatusNotification(online, applicationName);
        notificationManager.notify(McService.MAIN_NOTIFICATION_ID, notification);
    }

    @Override
    public Notification createConnectionStatusNotification(final boolean online, final String applicationName) {
        final ApplicationIcons icons = ((AndroidUI) Setup.get().getUI()).getApplicationIcons();
        final String text = getContext().getString(
                online ? R.string.mc_notification_status_online : R.string.mc_notification_status_offline);
        final String appName = applicationName == null ? Settings.get().getAppName() : applicationName;
        final Notification notification = new Notification(
                online ? icons.getOnline() : icons.getOffline(), appName, System.currentTimeMillis());
        final PendingIntent contentIntent = PendingIntent.getActivity(
                getContext(), 0, new Intent(getContext(), LoginActivity.class), 0);
        notification.setLatestEventInfo(getContext(), appName, text, contentIntent);
        setLastNotificationIsAlert(true);
        return notification;
    }

    @Override
    public Notification createGPSNotification() {
        final Notification notification = new Notification(
                ((AndroidUI) Setup.get().getUI()).getApplicationIcons().getAlert(),
                getContext().getString(R.string.mc_commons_notification_gpsDisabled), System.currentTimeMillis());
        final PendingIntent contentIntent = PendingIntent.getActivity(
                getContext(), 0, new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        notification.setLatestEventInfo(getContext(),
                getContext().getString(R.string.mc_commons_notification_gpsDisabled),
                getContext().getString(R.string.mc_commons_notification_gpsOpenSettings), contentIntent);
        setLastNotificationIsAlert(true);
        return notification;
    }
}
