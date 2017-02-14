package com.magenta.mc.client.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.components.dialogs.DialogCallback;
import com.magenta.mc.client.android.mc.components.dialogs.DialogCallbackExecutor;
import com.magenta.mc.client.android.mc.components.dialogs.SynchronousCallback;
import com.magenta.mc.client.android.mc.components.dialogs.manager.IDialogManager;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.util.FutureRunnable;

import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.Sync;

public class DialogManager implements IDialogManager {

    private static final int MESSAGES_ID = 1;

    private final Sync dialogSync = new Mutex();
    private final Context applicationContext;

    public DialogManager(final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void runAsyncDialogTask(final Runnable task) {
        MobileApp.runConsecutiveTask(new Runnable() {
            public void run() {
                runDialogTask(task);
            }
        });
    }

    public void runDialogTask(final Runnable task) {
        try {
            acquireDialogSync();
            task.run();
        } catch (InterruptedException e) {
            // shutdown
        } finally {
            releaseDialogSync();
        }
    }

    public void runDialogTask(final FutureRunnable taskWithFuture) {
        try {
            acquireDialogSync();
            taskWithFuture.run(new Runnable() {
                public void run() {
                    releaseDialogSync();
                }
            });
        } catch (InterruptedException e) {
            // shutdown
        }
    }

    public boolean confirmUnsafe(String title, String message) {
        return confirmSafe(title, message);
    }

    public boolean confirmUnsafe(String title, String message, DialogCallback callback) {
        return confirmSafe(title, message, callback);
    }

    public boolean confirmSafe(String title, String message) {
        return confirmSafe(title, message, null);
    }

    public boolean confirmSafe(final String title, final String message, final DialogCallback callback) {
        final Object mutex = new Object();
        final Boolean[] result = {null};
        final SynchronousCallback confirmationCallback = new SynchronousCallback() {
            public void done(boolean ok) {
                result[0] = ok;
                synchronized (mutex) {
                    mutex.notify();
                }
            }
        };
        final Thread asyncConfirmation = new Thread(new Runnable() {
            public void run() {
                asyncConfirmSafe(title, message, confirmationCallback);
            }
        }, "SyncDialogDaemon");
        asyncConfirmation.start();
        synchronized (mutex) {
            try {
                if (result[0] == null)
                    mutex.wait();
            } catch (InterruptedException e) {
                // ok
            }
        }
        boolean res = result[0];
        DialogCallbackExecutor.execCallback(callback, res);
        return res;
    }

    public void asyncConfirmSafe(String title, String message) {
        asyncConfirmSafe(title, message, null);
    }

    public void asyncConfirmSafe(final String title, final String message, final DialogCallback callback) {
        final Activity activity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                if (isUseTitle() && title != null) {
                    builder.setTitle(title);
                }
                builder
                        .setMessage(message)
                        .setCancelable(false)
                        .setNegativeButton(getLeftButtonText(), getClickListener(callback, getLeftButtonResult()))
                        .setPositiveButton(getRightButtonText(), getClickListener(callback, getRightButtonResult()))
                        .show();
            }
        });
    }

    public void messageUnsafe(String title, String msg) {
        messageSafe(title, msg);
    }

    public void messageUnsafe(String title, String msg, DialogCallback callback) {
        messageSafe(title, msg, null);
    }

    public void messageSafe(String title, String msg) {
        messageSafe(title, msg, null);
    }

    public void messageSafe(final String title, final String msg, DialogCallback callback) {
        final Object mutex = new Object();
        final Boolean[] result = {null};
        final SynchronousCallback confirmationCallback = new SynchronousCallback() {

            public void done(boolean ok) {
                result[0] = ok;
                synchronized (mutex) {
                    mutex.notify();
                }
            }
        };
        final Thread asyncConfirmation = new Thread(new Runnable() {
            public void run() {
                asyncMessageSafe(title, msg, confirmationCallback);
            }
        }, "SyncDialogDaemon");
        asyncConfirmation.start();
        synchronized (mutex) {
            try {
                if (result[0] == null)
                    mutex.wait();
            } catch (InterruptedException e) {
                // ok
            }
        }
        boolean res = result[0];
        DialogCallbackExecutor.execCallback(callback, res);
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

    public void acquireDialogSync() throws InterruptedException {
        MCLoggerFactory.getLogger(getClass()).trace("Acquiring dialog sync");
        dialogSync.acquire();
        MCLoggerFactory.getLogger(getClass()).trace("Dialog sync acquired");
    }

    public void releaseDialogSync() {
        MCLoggerFactory.getLogger(getClass()).trace("Releasing dialog sync");
        dialogSync.release();
        MCLoggerFactory.getLogger(getClass()).trace("Dialog sync released");
    }

    public void ShowDialogsAgain(Object o) {
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