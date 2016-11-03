package com.magenta.maxunits.mobile.dlib.service;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.magenta.maxunits.mobile.dlib.DistributionApplication;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.entity.LocationEntity;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.service.LocationService;
import com.magenta.maxunits.mobile.service.SaveLocationsService;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.utils.UserUtils;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;

import java.sql.SQLException;

public class SaveLocationService extends SaveLocationsService {

    int mSendToServerInterval = 180000;  //every 3 mins

    public void onCreate() {
        super.onCreate();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final long saveToDbInterval = MxSettings.getInstance().getSaveLocationInterval();
        Log.d("---dbg1", "saveToDbInterval " + saveToDbInterval);
        mCdt = new CountDownTimer(mSendToServerInterval, saveToDbInterval) {
            public void onTick(long millisUntilFinished) {
                boolean isTrackinfEnabled = preferences.getBoolean("tracking.enabled", true);
                boolean isUserIdEmpty = Settings.get().getUserId().isEmpty();
                if (isTrackinfEnabled && !isUserIdEmpty) {
                    LocationService locationService = ServicesRegistry.getLocationService();
                    if (locationService != null) {
                        Location loc = locationService.getLocation();
                        if (loc != null) {
                            LocationEntity entity = new LocationEntity();
                            entity.setUserId(UserUtils.cutComponentName(Settings.get().getUserId()));
                            entity.setLat(loc.getLatitude());
                            entity.setLon(loc.getLongitude());
                            entity.setDate(loc.getTime());
                            entity.setSpeed(loc.getSpeed());
                            try {
                                DistributionDAO.getInstance(DistributionApplication.getContext()).saveLocation(entity);
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