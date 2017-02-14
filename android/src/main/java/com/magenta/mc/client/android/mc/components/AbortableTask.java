package com.magenta.mc.client.android.mc.components;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.exception.OperationTimeoutException;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

import java.util.TimerTask;

public class AbortableTask implements Runnable {

    private final Object sync = new Object();
    private TimerTask wakeUpTimeout;
    private long timeout;
    private Runnable operation;
    private Runnable abortOperation;
    private boolean done = false;
    private Thread runnerThread;

    public AbortableTask(Runnable operation, Runnable abortOperation, long timeout) {
        this.operation = operation;
        this.abortOperation = abortOperation;
        this.timeout = timeout;
        wakeUpTimeout = new MCTimerTask() {
            public void runTask() {
                timeout();
            }
        };
    }

    private void done() {
        MCLoggerFactory.getLogger(getClass()).debug("AT: waking up");
        synchronized (sync) {
            if (!done) {
                done = true;
                wakeUpTimeout.cancel();
            } else {
                throw new OperationTimeoutException("AT: Operation Timeout");
            }
        }
        MCLoggerFactory.getLogger(getClass()).debug("AT: wakeup complete");
    }

    private void timeout() {
        boolean abort = false;
        synchronized (sync) {
            if (!done) {
                done = true;
                abort = true;
            }
        }
        if (abort) {
            abort();
            // todo: check if the following is helpful
            runnerThread.interrupt();
        }
    }

    public void run() {
        runnerThread = Thread.currentThread();
        MobileApp.getInstance().getTimer().schedule(wakeUpTimeout, timeout);
        Exception exception = null;
        try {
            runTask();
        } catch (Exception e) {
            exception = e;
        }
        done();
        if (exception != null) {
            throw new RuntimeException(exception);
        }
    }

    public void runTask() {
        operation.run();
    }

    public void abort() {
        if (abortOperation != null) {
            abortOperation.run();
        }
    }
}
