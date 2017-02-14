package com.magenta.mc.client.android.mc.components;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MCTimerTask extends TimerTask {

    protected static Timer timer() {
        return MobileApp.getInstance().getTimer();
    }

    protected boolean isAsynchronous() {
        return true;
    }

    public void run() {
        try {
            if (isAsynchronous()) {
                // run task asynchronously, not to block timer thread
                MobileApp.runTask(new Runnable() {
                    public void run() {
                        runTask();
                    }
                });
            } else {
                runTask();
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).warn("Exception while executing timer task");
            e.printStackTrace();
        }
    }

    public void runTask() {
    }
}