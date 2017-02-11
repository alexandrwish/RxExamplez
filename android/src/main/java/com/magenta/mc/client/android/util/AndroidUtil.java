package com.magenta.mc.client.android.util;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.tracking.GeoLocation;
import com.magenta.mc.client.tracking.GeoLocationSource;
import com.magenta.mc.client.util.PlatformUtil;

public class AndroidUtil implements PlatformUtil {

    protected final Context applicationContext;

    public AndroidUtil(final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean startConnection() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean closeConnection() {
        // TODO is really need?
        return true;
    }

    public GeoLocation getGeoLocation(final int locationMaxAge) {
        LocationManager locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
        final LocationProvider locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        if (locationProvider == null) {
            return null;
        }
        final Location location = locationManager.getLastKnownLocation(locationProvider.getName());
        if (checkLocationAge(location, locationMaxAge)) {
            final GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            final Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
            int satellitesCount = 0;
            for (final GpsSatellite satellite : satellites) {
                if (satellite.usedInFix()) {
                    satellitesCount++;
                }
            }
            return convertToGeoLocation(location, satellitesCount, GeoLocationSource.GPS);
        }
        return null;
    }

    public String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public void initLocationAPI() {
    }

    public void shutdownLocationAPI() {
    }

    protected boolean checkLocationAge(Location location, long locationMaxAge) {
        return location != null && System.currentTimeMillis() - location.getTime() <= locationMaxAge;
    }

    protected GeoLocation convertToGeoLocation(final Location location, final int satellitesCount, final GeoLocationSource source) {
        GeoLocation result = new GeoLocation(
                location.getTime(),
                location.getLatitude(),
                location.getLongitude(),
                location.getSpeed() * 3.6F,
                location.getBearing(),
                satellitesCount);
        result.setRetrieveTimestamp(System.currentTimeMillis() + Setup.get().getSettings().getTimeDelta());
        result.setSource(source);
        return result;
    }
}