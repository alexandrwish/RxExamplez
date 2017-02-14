package com.magenta.mc.client.android.mc.components.waiting;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.exception.OperationTimeoutException;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.util.FutureRunnable;

import java.util.TimerTask;

public class LongOperationWithTimeout implements Runnable {
    private final Object sync = new Object();
    private Runnable hideWaitIconFuture;
    private TimerTask wakeUpTimeout;
    private long timeout;
    private Runnable operation;

    public LongOperationWithTimeout(Runnable operation, long timeout) {
        this.operation = operation;
        this.timeout = timeout;
    }


    public void done() {
        MCLoggerFactory.getLogger(getClass()).debug("LO: waking up");
        boolean runWakeUp = false;
        Runnable hideWaitIconFutureRef = null;
        synchronized (sync) {
            if (hideWaitIconFuture != null) {
                wakeUpTimeout.cancel();
                runWakeUp = true;
                hideWaitIconFutureRef = hideWaitIconFuture;
                hideWaitIconFuture = null;
            }
        }
        if (runWakeUp) {
            hideWaitIconFutureRef.run();
        }
        MCLoggerFactory.getLogger(getClass()).debug("LO: wakeup complete");
    }

    protected void abnormalCompletion(Exception e) {
        done();
        e.printStackTrace();
        MCLoggerFactory.getLogger(getClass()).debug("LO: exception, showing warning");
        Setup.get().getUI().getDialogManager().asyncMessageSafe(MobileApp.localize("Warning"), MobileApp.localize("msg.cannot_request"));
    }

    public void run() {
        WaitIcon.show(new FutureRunnable() {
            public void run(Runnable hideWaitIconFuture) {
                try {
                    LongOperationWithTimeout.this.hideWaitIconFuture = hideWaitIconFuture;
                    wakeUpTimeout = new MCTimerTask() {
                        public void runTask() {
                            abnormalCompletion(new OperationTimeoutException("LO: Operation Timeout"));
                        }
                    };
                    operation.run();
                    MobileApp.getInstance().getTimer().schedule(wakeUpTimeout, timeout);
                } catch (Exception e) {
                    abnormalCompletion(e);
                }
            }
        });
    }
}