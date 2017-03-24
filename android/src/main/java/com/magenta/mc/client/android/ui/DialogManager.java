package com.magenta.mc.client.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.magenta.mc.client.android.components.dialogs.DialogCallback;
import com.magenta.mc.client.android.components.dialogs.DialogCallbackExecutor;
import com.magenta.mc.client.android.components.dialogs.manager.IDialogManager;
import com.magenta.mc.client.android.setup.Setup;

import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.Sync;

public class DialogManager implements IDialogManager {

    private static final int MESSAGES_ID = 1;

    private final Sync dialogSync = new Mutex();
    private final Context applicationContext;

    public DialogManager(final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void asyncMessageSafe(final String title, final String msg) {
        asyncMessageSafe(title, msg, null);
    }

    public void asyncMessageSafe(final String title, final String msg, final DialogCallback callback) {
        final Activity activity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    if (isUseTitle() && title != null) {
                        builder.setTitle(title);
                    }
                    builder
                            .setMessage(msg)
                            .setCancelable(false)
                            .setNeutralButton(getDefaultButtonText(), getClickListener(callback, getDefaultButtonResult()))
                            .create()
                            .show();
                }
            });
        } else {
            new Notifications.NotificationBuilder(applicationContext)
                    .setTitle(title)
                    .setMessage(msg)
                    .setIcon(((AndroidUI) Setup.get().getUI()).getApplicationIcons().getAlert())
                    .setId(MESSAGES_ID)
                    .show();
            if (callback != null) {
                DialogCallbackExecutor.execCallback(callback, true);
            }
        }
    }

    public void showDialogsAgain(Object o) {
        //TODO implement this method if need to show dialogs after pull up
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public Sync getDialogSync() {
        return dialogSync;
    }

    private DialogInterface.OnClickListener getClickListener(final DialogCallback callback, final boolean result) {
        return new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                if (callback != null) {
                    DialogCallbackExecutor.execCallback(callback, result);
                }
            }
        };
    }

    protected String getDefaultButtonText() {
        return "OK";
    }

    protected String getLeftButtonText() {
        return "Cancel";
    }

    protected String getRightButtonText() {
        return "Ok";
    }

    protected boolean getDefaultButtonResult() {
        return true;
    }

    protected boolean getLeftButtonResult() {
        return false;
    }

    protected boolean getRightButtonResult() {
        return true;
    }

    protected boolean isUseTitle() {
        return true;
    }
}