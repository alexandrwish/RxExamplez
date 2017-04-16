package com.magenta.mc.client.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.util.MxAndroidUtil;

import java.sql.SQLException;

public class SaveLocationsService extends Service {

    private CountDownTimer mCdt = null;
    private boolean mKillMePlz = false;

    public void onCreate() {
        super.onCreate();
        final long saveToDbInterval = Settings.get().getLocationSave() * 1000;
        mCdt = new CountDownTimer(saveToDbInterval, saveToDbInterval) {
            public void onTick(long millisUntilFinished) {
                LocationEntity loc = MxAndroidUtil.getGeoLocation();
                if (loc != null) {
                    try {
                        DistributionDAO.getInstance().saveLocation(loc);
                    } catch (SQLException e) {
                        MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                    }
                }
            }

            public void onFinish() {
                if (!mKillMePlz) {
                    mCdt.start();
                }
            }
        };
        mCdt.start();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        mKillMePlz = true;
        mCdt.cancel();
    }
}