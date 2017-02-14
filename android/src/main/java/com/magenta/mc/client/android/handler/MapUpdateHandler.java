package com.magenta.mc.client.android.handler;

import android.os.Handler;
import android.os.Looper;

public abstract class MapUpdateHandler extends Handler {

    private static final long UPDATE_MAP_PERIOD = 20 * 1000;

    private MapletUpdateRunnable runnable;
    private boolean killMePlease = false;
    private boolean firstRun = true;

    protected MapUpdateHandler() {
        super(Looper.getMainLooper());
        runnable = new MapletUpdateRunnable();
    }

    public void start() {
        killMePlease = false;
        post(runnable);
    }

    public void stop() {
        killMePlease = true;
        removeCallbacks(runnable);
    }

    protected abstract void updateMap(boolean firstRun);

    private class MapletUpdateRunnable implements Runnable {

        public void run() {
            if (!killMePlease) {
                updateMap(firstRun);
                postDelayed(this, UPDATE_MAP_PERIOD);
                if (firstRun) {
                    firstRun = false;
                }
            }
        }
    }
}