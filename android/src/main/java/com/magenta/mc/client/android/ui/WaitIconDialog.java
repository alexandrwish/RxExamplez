package com.magenta.mc.client.android.ui;

import android.app.Activity;
import android.app.ProgressDialog;

import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.components.dialogs.DialogCallback;
import com.magenta.mc.client.components.dialogs.DialogCallbackExecutor;
import com.magenta.mc.client.components.waiting.WaitIcon;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.FutureRunnable;

public class WaitIconDialog extends WaitIcon {

    private DialogCallback callback;
    private ProgressDialog dialog;
    private FutureRunnable task;

    public WaitIconDialog() {
    }

    public WaitIconDialog(final FutureRunnable task) {
        this.task = task;
    }

    public WaitIconDialog(final FutureRunnable task, final DialogCallback callback) {
        this.task = task;
        this.callback = callback;
    }

    public WaitIconDialog(final DialogCallback callback) {
        this.callback = callback;
    }

    public void setCallback(final DialogCallback callback) {
        this.callback = callback;
    }

    public WaitIcon create(final FutureRunnable task, final DialogCallback callback) {
        return new WaitIconDialog(task, callback);
    }

    public void show() {
        show(false);
    }

    public void show(final boolean inDaemonThread) {
        final Activity activity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog = new ProgressDialog(activity);
                        dialog.setMessage(MobileApp.localize("msg.hourglas"));
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.show();
                        final Thread processingThread = new Thread(new Runnable() {
                            public void run() {
                                processing(getHideRunnable());
                            }
                        });
                        processingThread.setDaemon(inDaemonThread);
                        processingThread.start();
                    }
                });
            } else {
                MCLoggerFactory.getLogger(getClass()).error(String.format("Activity \"%s\" is finished..", activity.getLocalClassName()));
            }
        }
    }

    private Runnable getHideRunnable() {
        return new Runnable() {
            public void run() {
                ((AndroidUI) Setup.get().getUI()).runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                        DialogCallbackExecutor.execCallback(callback, true);
                    }
                });
            }
        };
    }

    protected void setShowingPaused0(final boolean pause) {
        ((AndroidUI) Setup.get().getUI()).runOnUiThread(new Runnable() {
            public void run() {
                if (pause) {
                    dialog.hide();
                } else {
                    dialog.show();
                }
            }
        });
    }

    private void processing(final Runnable hideRunnable) {
        if (task != null) {
            task.run(hideRunnable);
        } else {
            hideRunnable.run();
        }
    }
}