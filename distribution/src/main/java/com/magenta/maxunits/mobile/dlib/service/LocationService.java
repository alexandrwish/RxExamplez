package com.magenta.maxunits.mobile.dlib.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener {

    static Location mPrevLocationF;
    static Location mFalseLocation;
    static int mTimeThreshold = 30000;
    static int mAccuracyPercent = 40;
    static int mAccuracyMeters = 10;
    static int mVelocityThreshold = 33;
    final IBinder mBinder = new LocalBinder();
    LocationManager mLocationManager;
    Location mCurrentLocation;
    Timer mLocationUpdateTimer;
    long mGetLastKnownLocationTimer = 5000;
    long mLocationUpdateSeconds = 20;
    long mLocationUpdateMeters = 20;

    private static boolean filterLocation(Location currentLocation) {
        if (mPrevLocationF == null) {
            mPrevLocationF = currentLocation;
            return true;
        } else {
            if (mFalseLocation != null) {
                if (currentLocation.getLongitude() == mFalseLocation.getLongitude() && currentLocation.getLatitude() == mFalseLocation.getLatitude()) {
                    return false;
                }
            }
            if (currentLocation.getLongitude() == mPrevLocationF.getLongitude() && currentLocation.getLatitude() == mPrevLocationF.getLatitude()) {
                mPrevLocationF = currentLocation;
                mFalseLocation = null;
                return true;
            }

            float currentAccuracy = currentLocation.getAccuracy();
            float previousAccuracy = mPrevLocationF.getAccuracy();
            float velocity;
            float accuracyDifference = Math.abs(previousAccuracy - currentAccuracy);
            boolean lowerAccuracyAcceptable = currentAccuracy > previousAccuracy && mPrevLocationF.getProvider().equals(currentLocation.getProvider()) && ((mAccuracyPercent >= 100 * accuracyDifference / previousAccuracy) || accuracyDifference < mAccuracyMeters);
            float[] results = new float[1];
            Location.distanceBetween(mPrevLocationF.getLatitude(), mPrevLocationF.getLongitude(), currentLocation.getLatitude(), currentLocation.getLongitude(), results);
            float distance = results[0] - (currentLocation.getAccuracy() + mPrevLocationF.getAccuracy());
            long timeDelta = (currentLocation.getTime() - mPrevLocationF.getTime()) / 1000;
            if (timeDelta == 0) {
                velocity = 0;
            } else {
                velocity = distance / timeDelta;
            }
            if (velocity <= mVelocityThreshold && (currentAccuracy <= previousAccuracy || (currentLocation.getTime() - mPrevLocationF.getTime()) > mTimeThreshold || lowerAccuracyAcceptable)) {
                mPrevLocationF = currentLocation;
                mFalseLocation = null;
                return true;
            } else {
                mFalseLocation = currentLocation;
                return false;
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final List<String> enabledProviders = mLocationManager.getProviders(true);
        for (String provider : enabledProviders) {
            mLocationManager.requestLocationUpdates(provider, mLocationUpdateSeconds, mLocationUpdateMeters, this);
        }
        mLocationUpdateTimer = new Timer();
        mLocationUpdateTimer.schedule(new TimerTask() {

            public void run() {
                for (String provider : enabledProviders) {
                    Location loc = mLocationManager.getLastKnownLocation(provider);
                    if (loc != null) {
                        loc.setTime(System.currentTimeMillis());
                        if (filterLocation(loc)) {
                            mCurrentLocation = loc;
                        }
                    }
                }
            }
        }, 5000, mGetLastKnownLocationTimer);

    }

    public void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Location getLocation() {
        return mCurrentLocation;
    }

    public void onLocationChanged(Location location) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}