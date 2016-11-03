package com.magenta.mc.client.components;

import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.log.MCLoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created 14.05.2010
 *
 * @author Konstantin Pestrikov
 */
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
