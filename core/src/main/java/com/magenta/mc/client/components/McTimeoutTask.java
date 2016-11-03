package com.magenta.mc.client.components;

/**
 * Created with IntelliJ IDEA.
 * User: const
 * Date: 06.03.13
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
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
