package com.magenta.mc.client.android.components.dialogs;

import com.magenta.mc.client.android.MobileApp;

public class DialogCallbackExecutor {

    public static void execCallback(final DialogCallback callback, final boolean result) {
        if (callback != null) {
            if (callback instanceof SynchronousCallback) {
                callback.done(result);
            } else {
                MobileApp.runTask(new Runnable() {
                    public void run() {
                        callback.done(result);
                    }
                });
            }
        }
    }
}