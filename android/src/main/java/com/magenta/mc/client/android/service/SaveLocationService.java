package com.magenta.mc.client.android.service;

import android.location.Location;
import android.os.CountDownTimer;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

import java.sql.SQLException;

public class SaveLocationService extends SaveLocationsService {

    int mSendToServerInterval = 180000;  //every 3 mins

    public void onCreate() {
        super.onCreate();
        final long saveToDbInterval = Settings.get().getLocationSave();
        mCdt = new CountDownTimer(mSendToServerInterval, saveToDbInterval) {
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
}