package com.magenta.mc.client.android.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.log.MCLoggerFactory;

import java.sql.SQLException;

public class SaveLocationsService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private CountDownTimer mCdt = null;
    private boolean mKillMePlz = false;
    private static final int SEND_TO_SERVER_INTERVAL = 180000;  //every 3 mins

    public void onCreate() {
        super.onCreate();
        final long saveToDbInterval = Settings.get().getLocationSave();
        mCdt = new CountDownTimer(SEND_TO_SERVER_INTERVAL, saveToDbInterval) {
            public void onTick(long millisUntilFinished) {
                if (Settings.get().getLocationEnable()) {
                    LocationService locationService = ServicesRegistry.getLocationService();
                    if (locationService != null) {
                        Location loc = locationService.getLocation();
                        if (loc != null) {
                            LocationEntity entity = new LocationEntity();
                            entity.setUserId(Settings.get().getUserId());
                            entity.setLat(loc.getLatitude());
                            entity.setLon(loc.getLongitude());
                            entity.setDate(loc.getTime());
                            entity.setSpeed(loc.getSpeed());
                            try {
                                DistributionDAO.getInstance().saveLocation(entity);
                            } catch (SQLException e) {
                                MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                            }
                        }
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