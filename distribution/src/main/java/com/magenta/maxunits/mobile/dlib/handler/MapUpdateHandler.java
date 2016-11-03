package com.magenta.maxunits.mobile.dlib.handler;

import android.os.Handler;
import android.os.Looper;

/**
 * Project: PTS
 * Author: komarov
 * Date 24.07.14
 * <p/>
 * Copyright (c) 1999-2014 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 */
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

    @Deprecated
    public void setKillMePlease(boolean killMePlease) {
        this.killMePlease = killMePlease;
    }

    protected abstract void updateMap(boolean firstRun);

    private class MapletUpdateRunnable implements Runnable {

        @Override
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