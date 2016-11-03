package com.magenta.mc.client.android.setup;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.tracking.GeoLocation;
import com.magenta.mc.client.tracking.GeoLocationService;
import com.magenta.mc.client.tracking.GeoLocationSource;

import java.text.SimpleDateFormat;

/**
 * @autor Petr Popov
 * Created 18.07.12 18:04
 */
public class AndroidUtil2 extends AndroidUtil {

    public static final int LOCATION_SOURCE_LOG_TIMEOUT = 1 * 60 * 1000; // 1 minute
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private final LocationListener gpsLocationListener;
    private final LocationListener networkLocationListener;
    private final Handler handler;
    private Location gpsLocation;
    private long gpsElapsedRealtime;
    private Location networkLocation;
    private long networkElapsedRealtime;
    private Location location;
    private long locationElapsedRealtime;
    private long locationSourceLogElapsedRealtime;

    public AndroidUtil2(final Context applicationContext) {
        super(applicationContext);
        gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the gps location provider.
                gpsElapsedRealtime = SystemClock.elapsedRealtime();
                AndroidUtil2.this.gpsLocation = location;
                GeoLocationService.getInstance().notifyLocationUpdateListeners();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        networkLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                networkElapsedRealtime = SystemClock.elapsedRealtime();
                AndroidUtil2.this.networkLocation = location;
                GeoLocationService.getInstance().notifyLocationUpdateListeners();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        handler = new Handler();
    }

    private void logLocationSource(String locationSource) {
        long currentElapsedRealtime = SystemClock.elapsedRealtime();
        if (currentElapsedRealtime - locationSourceLogElapsedRealtime > LOCATION_SOURCE_LOG_TIMEOUT) {
            locationSourceLogElapsedRealtime = currentElapsedRealtime;
            MCLoggerFactory.getLogger(AndroidUtil2.class).debug("Location source: " + locationSource);
        }
    }

    @Override
    public void initLocationAPI() {
        final LocationManager locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
        handler.post(new Runnable() {
            public void run() {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
            }
        });
    }

    @Override
    public void shutdownLocationAPI() {
        super.shutdownLocationAPI();
        final LocationManager locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
        handler.post(new Runnable() {
            public void run() {
                locationManager.removeUpdates(gpsLocationListener);
                locationManager.removeUpdates(networkLocationListener);
            }
        });
    }

    public GeoLocation getGeoLocation(final int locationMaxAge) {
        LocationManager locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
        final LocationProvider gpsLocationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        final LocationProvider networkLocationProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
        if (gpsLocationProvider == null && networkLocationProvider == null) {
            return null;
        }
        Location location = this.location;
        long locationElapsedRealtime = this.locationElapsedRealtime;
        Location networkLocation = this.networkLocation;
        long networkElapsedRealtime = this.networkElapsedRealtime;
        Location gpsLocation = this.gpsLocation;
        long gpsElapsedRealtime = this.gpsElapsedRealtime;

        GeoLocationSource source = GeoLocationSource.UNKNOWN;
        String locationSource = "last";
        if (location == null && networkLocation == null && gpsLocation == null) {
            // no location received yet from LocationManager, try to stick to last known location
            Location lastKnownGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownGpsLocation != null || lastKnownNetworkLocation != null) {
                if (lastKnownGpsLocation == null) {
                    location = lastKnownNetworkLocation;
                    locationElapsedRealtime = SystemClock.elapsedRealtime();
                    locationSource = "lastNetwork";
                    source = GeoLocationSource.LBS;
                } else {
                    location = lastKnownGpsLocation;
                    locationElapsedRealtime = SystemClock.elapsedRealtime();
                    locationSource = "lastGPS";
                    source = GeoLocationSource.GPS;
                }
            }
        } else {
            if (location == null) {
                location = networkLocation;
                locationElapsedRealtime = networkElapsedRealtime;
                locationSource = "network";
                source = GeoLocationSource.LBS;
            } else if (networkLocation != null && isBetterLocation(networkLocation, location, networkElapsedRealtime, locationElapsedRealtime)) {
                location = networkLocation;
                locationElapsedRealtime = networkElapsedRealtime;
                locationSource = "network";
                source = GeoLocationSource.LBS;
            }
            if (location == null) {
                location = gpsLocation;
                locationElapsedRealtime = gpsElapsedRealtime;
                locationSource = "GPS";
                source = GeoLocationSource.GPS;
            } else if (gpsLocation != null && isBetterLocation(gpsLocation, location, gpsElapsedRealtime, locationElapsedRealtime)) {
                location = gpsLocation;
                locationElapsedRealtime = gpsElapsedRealtime;
                locationSource = "GPS";
                source = GeoLocationSource.GPS;
            }
        }
        this.location = location;
        this.locationElapsedRealtime = locationElapsedRealtime;
        if (checkLocationAge(location, locationMaxAge)) {
            int satellitesCount = 0;
            if (location != null && LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
                final GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                final Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                for (final GpsSatellite satellite : satellites) {
                    if (satellite.usedInFix()) {
                        satellitesCount++;
                    }
                }
            }
            logLocationSource(locationSource);
            return convertToGeoLocation(location, satellitesCount, source);
        }
        logLocationSource("not found");
        return null;
    }

    /**
     * check out here http://developer.android.com/guide/topics/location/strategies.html
     * <p/>
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location               The new Location that you want to evaluate
     * @param currentBestLocation    The current Location fix, to which you want to compare the new one
     * @param newElapsedRealtime
     * @param currentElapsedRealtime
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation, long newElapsedRealtime, long currentElapsedRealtime) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long newLocationTime = location.getTime();
        long currentLocationTime = currentBestLocation.getTime();

        SimpleDateFormat df = new SimpleDateFormat();
        //MCLoggerFactory.getLogger(AndroidUtil2.class).debug("new location time: " + df.format(new Date(newLocationTime)) + ", elapsed: " + newElapsedRealtime);
        //MCLoggerFactory.getLogger(AndroidUtil2.class).debug("current location time: " + df.format(new Date(currentLocationTime)) + ", elapsed: " + currentElapsedRealtime);

        long timeDelta = newElapsedRealtime - currentElapsedRealtime;
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        String newLocationProvider = location.getProvider();
        String currentLocationProvider = currentBestLocation.getProvider();

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            //MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is newer than " + currentLocationProvider + ", time delta " + timeDelta);
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            //MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is older than " + currentLocationProvider + ", time delta " + timeDelta);
            return false;
        }

        // Check whether the new location fix is more or less accurate
        float newLocationAccuracy = location.getAccuracy();
        float currentLocationAccuracy = currentBestLocation.getAccuracy();
        //MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " location accuracy: " + newLocationAccuracy);
        //MCLoggerFactory.getLogger(AndroidUtil2.class).debug(currentLocationProvider + " location accuracy: " + currentLocationAccuracy);

        float accuracyDelta = newLocationAccuracy - currentLocationAccuracy;
        boolean isLessAccurate = false;
        boolean isMoreAccurate = false;
        boolean isSignificantlyLessAccurate = false;
        if (newLocationAccuracy == 0 || currentLocationAccuracy == 0) {
            if (newLocationAccuracy == 0 && currentLocationAccuracy > 0) {
                isSignificantlyLessAccurate = true;
                isLessAccurate = true;
            } else if (newLocationAccuracy > 0 && currentLocationAccuracy == 0) {
                isMoreAccurate = true;
            }
        } else {
            isLessAccurate = accuracyDelta > 0;
            isMoreAccurate = accuracyDelta < 0;
            isSignificantlyLessAccurate = accuracyDelta > 200;
        }

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocationProvider,
                currentLocationProvider);

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            //MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is more accurate than " + currentLocationProvider + ", accuracyDelta: " + accuracyDelta);
            return true;
        } else if (isNewer && !isLessAccurate) {
            //MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is newer and not less accurate than " + currentLocationProvider + ", accuracyDelta: " + accuracyDelta);
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            //MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is newer and not significantly less accurate and same provider than " + currentLocationProvider + ", accuracyDelta: " + accuracyDelta);
            return true;
        }/* else {
            if (isLessAccurate) {
                if (isSignificantlyLessAccurate) {
                    MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is significantly less accurate than " + currentLocationProvider + ", accuracyDelta: " + accuracyDelta);
                } else {
                    MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is less accurate than " + currentLocationProvider + ", accuracyDelta: " + accuracyDelta);
                }
            } else {
                MCLoggerFactory.getLogger(AndroidUtil2.class).debug(newLocationProvider + " is more accurate than " + currentLocationProvider + ", accuracyDelta: " + accuracyDelta + ", but it's older");
            }
        }*/
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
