package com.magenta.mc.client.components.waiting;

import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.components.MCTimerTask;
import com.magenta.mc.client.exception.OperationTimeoutException;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.FutureRunnable;

import java.util.TimerTask;

/**
 * Created 23.12.2010
 *
 * @author Konstantin Pestrikov
 */
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
