package com.magenta.mc.client.android.mc;

import android.content.Context;
import android.widget.Toast;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.entity.LocationEntity;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.tomtom.navapp.ErrorCallback;
import com.tomtom.navapp.NavAppClient;
import com.tomtom.navapp.NavAppError;
import com.tomtom.navapp.Routeable;
import com.tomtom.navapp.Trip;

import java.util.Locale;
import java.util.Map;

public class MXNavApp implements Routeable.Listener {

    private static final String FORMATTED_STRING = "Distance between A [%f ; %f] and B [%f ; %f] = %f.";
    private static final String FORMATTED_EXCEPTION = "Can't calculate distance between A [%f ; %f] and B [%f ; %f].";
    private static final Double EARTH_RADIUS_KM = 6372.795; //in km
    private static final Integer METERS_IN_KM = 1000;
    private static MXNavApp instance;
    private final Map.Entry[] entries = new Map.Entry[2];
    protected NavAppClient client;

    private MXNavApp(final Context context) {
        client = NavAppClient.Factory.make(context, new ErrorCallback() {

            public void onError(NavAppError navAppError) {
                onTomTomError(navAppError);
            }
        });
    }

    public static MXNavApp init(final Context context) {
        instance = new MXNavApp(context);
        return instance;
    }

    @Deprecated
    public static MXNavApp getInstance() {
        return instance;
    }

    private static Double getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        try {
            Double cosLat1 = Math.cos(lat1 * Math.PI / 180);
            Double cosLat2 = Math.cos(lat2 * Math.PI / 180);
            Double sinLat1 = Math.sin(lat1 * Math.PI / 180);
            Double sinLat2 = Math.sin(lat2 * Math.PI / 180);
            Double delta = (lon1 * Math.PI / 180) - (lon2 * Math.PI / 180);
            Double cosDelta = Math.cos(delta);
            Double sinDelta = Math.sin(delta);
            Double y = Math.sqrt(Math.pow(cosLat2 * sinDelta, 2) + Math.pow(cosLat1 * sinLat2 - sinLat1 * cosLat2 * cosDelta, 2));
            Double x = sinLat1 * sinLat2 + cosLat1 * cosLat2 * cosDelta;
            Double length = Math.atan2(y, x) * EARTH_RADIUS_KM * METERS_IN_KM;
            MCLoggerFactory.getLogger().debug(String.format(Locale.UK, FORMATTED_STRING, lat1, lon1, lat2, lon2, length));
            return length;
        } catch (Exception e) {
            MCLoggerFactory.getLogger().error(String.format(Locale.UK, FORMATTED_EXCEPTION, lat1, lon1, lat2, lon2), e);
            return -1.0;
        }
    }

    private void onTomTomError(final NavAppError navAppError) {
        MCLoggerFactory.getLogger(getClass()).error("Error: " + (navAppError != null ? navAppError.getErrorMessage() : ""));
    }

    public void planTrip(double lat, double lon, Trip.PlanListener listener) {
        if (client == null) return;
        Routeable routeable = client.makeRouteable(lat, lon);
        client.getTripManager().planTrip(routeable, listener);
        Toast.makeText(McAndroidApplication.getInstance(), R.string.mx_tomtom_route, Toast.LENGTH_LONG).show();
    }

    public void registerCurrentLocationListener() {
        if (client == null) return;
        client.getLocationManager().registerCurrentLocationListener(this);
    }

    public void unregisterCurrentLocationListener() {
        if (client == null) return;
        client.getLocationManager().unregisterCurrentLocationListener(this);
    }

    public void destroy() {
        if (client == null) return;
        client.close();
        client = null;
    }

    public LocationEntity getLocation(int maxAge) {
        if (entries[0] != null) {
            Map.Entry e0 = entries[0];
            LocationEntity location = null;
            if (entries[1] == null) {
                if (System.currentTimeMillis() - ((Long) (e0.getKey())) <= maxAge) {
                    Routeable r = (Routeable) e0.getValue();
                    location = new LocationEntity();
                    location.setDate((Long) e0.getKey());
                    location.setLat(r.getLatitude());
                    location.setLon(r.getLongitude());
                    location.setSpeed(0f);
                    location.setUserId(Settings.get().getUserId());
                }
            } else {
                Map.Entry e1 = entries[1];
                if (System.currentTimeMillis() - ((Long) (e1.getKey())) <= maxAge) {
                    Routeable r0 = (Routeable) e0.getValue();
                    Routeable r1 = (Routeable) e1.getValue();
                    location = new LocationEntity();
                    location.setDate((Long) e1.getKey());
                    location.setLat(r1.getLatitude());
                    location.setLon(r1.getLongitude());
                    location.setSpeed(Float.valueOf((getDistance(r0.getLatitude(), r0.getLongitude(), r1.getLatitude(), r1.getLongitude()) / (((Long) e0.getKey() - (Long) e1.getKey() + 1) / 1000)) + ""));
                    location.setUserId(Settings.get().getUserId());
                }
            }
            return location;
        }
        return null;
    }

    public void onRoutable(final Routeable routeable) {
        Map.Entry entry = new Map.Entry<Long, Routeable>() {
            public Long getKey() {
                return System.currentTimeMillis();
            }

            public Routeable getValue() {
                return routeable;
            }

            public Routeable setValue(Routeable value) {
                return null;
            }
        };
        if (entries[0] != null) {
            Routeable r = (Routeable) entries[0].getValue();
            if (routeable.getLatitude() == r.getLatitude() && routeable.getLongitude() == r.getLongitude())
                return;
            entries[1] = entries[0];
        }
        entries[0] = entry;
    }
}