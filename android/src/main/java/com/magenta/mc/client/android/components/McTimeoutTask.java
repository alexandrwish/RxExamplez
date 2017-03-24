package com.magenta.mc.client.android.components;

public class McTimeoutTask extends MCTimerTask {

    private Runnable task;

    public McTimeoutTask(Runnable task, long timeoutMillis) {
        this.task = task;
        timer().schedule(this, timeoutMillis);
    }

    public void runTask() {
        task.run();
    }
}