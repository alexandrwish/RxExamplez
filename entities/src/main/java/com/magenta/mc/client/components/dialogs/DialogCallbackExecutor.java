package com.magenta.mc.client.components.dialogs;

import com.magenta.mc.client.MobileApp;

/**
 * @author: Petr Popov
 * Created: 14.10.11 16:55
 */
public class DialogCallbackExecutor {

    public static void execCallback(final DialogCallback callback, final boolean result) {
        if (callback != null) {
            if (callback instanceof SynchronousCallback) {
                callback.done(result);
            } else if (callback instanceof ConsecutiveCallback) {
                MobileApp.runConsecutiveTask(new Runnable() {
                    public void run() {
                        callback.done(result);
                    }
                });
            } else if (callback instanceof DialogCallback) {
                MobileApp.runTask(new Runnable() {
                    public void run() {
                        callback.done(result);
                    }
                });
            }
        }
    }

}
