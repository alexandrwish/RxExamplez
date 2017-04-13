package com.magenta.mc.client.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.log.MCLoggerFactory;

import java.sql.SQLException;

public class SaveLocationsService extends Service {

    private static final int SEND_TO_SERVER_INTERVAL = 180000;  //every 3 mins
    private final IBinder mBinder = new LocalBinder();
    private CountDownTimer mCdt = null;
    private boolean mKillMePlz = false;

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
                            entity.setToken(Settings.get().getAuthToken());
                            entity.setGps(isGPSEnable());
                            entity.setDate(loc.getTime());
                            entity.setSpeed(loc.getSpeed());
                            entity.setGprs(getNetworkInfo());
                            entity.setLat(loc.getLatitude());
                            entity.setLon(loc.getLongitude());
                            entity.setBattery(getBatteryLevel());
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

    private Double getBatteryLevel() {
        Intent batteryIntent = McAndroidApplication.getInstance().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            return -1d;
        }
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level == -1 || scale == -1) {
            return -1d;
        }
        return ((float) level / (float) scale) * 100.0d;
    }

    private boolean isGPSEnable() {
        LocationManager manager = (LocationManager) McAndroidApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private String getNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) McAndroidApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null ? activeNetworkInfo.toString() : "[]";
    }

    public class LocalBinder extends Binder {

        public SaveLocationsService getService() {
            return SaveLocationsService.this;
        }
    }
}