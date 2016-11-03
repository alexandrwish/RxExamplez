package com.magenta.maxunits.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

public class SaveLocationsService extends Service {

    protected final IBinder mBinder = new LocalBinder();
    protected CountDownTimer mCdt = null;
    protected boolean mKillMePlz = false;

    public void kill() {
        mKillMePlz = true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onDestroy() {
        super.onDestroy();
        mKillMePlz = true;
        mCdt.cancel();
    }

    public class LocalBinder extends Binder {

        public SaveLocationsService getService() {
            return SaveLocationsService.this;
        }
    }
}