package com.magenta.mc.client.android.ui;

import android.app.Activity;
import android.content.Context;

import com.magenta.mc.client.android.components.dialogs.manager.IDialogManager;
import com.magenta.mc.client.android.components.waiting.WaitIcon;
import com.magenta.mc.client.android.log.MCLoggerFactory;

public class AndroidUI implements UI {

    protected final Context applicationContext;
    protected ApplicationIcons applicationIcons;
    protected Notifications notifications;
    private DialogManager dialogManager;
    private Activity currentActivity;

    public AndroidUI(final Context applicationContext) {
        this.applicationContext = applicationContext;
        initDialogManager(this.applicationContext);
        WaitIcon.implement(new WaitIconDialog(null, null));
        initNotifications(applicationContext);
        initNotificationIcons();
    }

    private void initDialogManager(Context context) {
        dialogManager = new DialogManager(context);
    }

    protected void initNotifications(Context context) {
        notifications = new Notifications(context);
    }

    private void initNotificationIcons() {
        applicationIcons = new SmokeApplicationIcons();
    }

    public ApplicationIcons getApplicationIcons() {
        return applicationIcons;
    }

    public IDialogManager getDialogManager() {
        return dialogManager;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void switchToActivity(final Activity currentActivity) {
        MCLoggerFactory.getLogger(getClass()).debug("Switch to activity " + currentActivity);
        this.currentActivity = currentActivity;
    }

    public void runOnUiThread(final Runnable task) {
        final Activity activity = getCurrentActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        task.run();
                    } catch (Exception e) {
                        MCLoggerFactory.getLogger(getClass()).error("Warning!!!", e.getCause());
                    }
                }
            });
        } else {
            throw new IllegalStateException("no current activity found to run task on it's UI thread");
        }
    }

    public Notifications getNotifications() {
        return notifications;
    }

    public void shutdown() {
    }

    public void toFront() {
    }

    public boolean beforeLoginActivity(Activity activity) {
        return true;
    }
}