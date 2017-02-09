package com.magenta.maxunits.mobile.dlib.mc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.dlib.MxApplication;
import com.magenta.maxunits.mobile.dlib.entity.LocationEntity;
import com.magenta.maxunits.mobile.dlib.service.LocationService;
import com.magenta.maxunits.mobile.dlib.service.ServicesRegistry;
import com.magenta.maxunits.mobile.dlib.utils.ReflectionUtils;
import com.magenta.maxunits.mobile.entity.Address;
import com.magenta.mc.client.android.setup.AndroidUtil2;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.tracking.GeoLocation;
import com.tomtom.navapp.Trip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class MxAndroidUtil {

    private static MxAndroidUtil util;

    protected MxAndroidUtil() {
        util = this;
    }

    public static MxAndroidUtil getUtil() {
        return util;
    }

    public static String getPhoneNumber() {
        return ((TelephonyManager) MxApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
    }

    public static String getImei() {
        final String imei = ((TelephonyManager) MxApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return imei == null ? "" : imei;
    }

    public static boolean showGPSNavigator(final Address address, final Context context) {
        try {
            Intent intent = MxAndroidUtil.startGPSNavigator(address);
            if (intent != null) {
                context.startActivity(intent);
            } else {
                MCLoggerFactory.getLogger().info("Can't load location");
                Toast.makeText(context, context.getString(R.string.mx_msg_error_no_current_gps_location), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger().error("Stub!!", e);
            Toast.makeText(context, context.getString(R.string.mx_msg_error_with_start_gps), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean showGoogleOrYandexNavigator(final Address address, final Context context) {
        try {
            Intent intent = ((MxSettings) Setup.get().getSettings()).getLocale().equalsIgnoreCase("ru")
                    ? launchYandexNavigator(context, address)
                    : launchGoogleNavigator(address);
            if (intent != null) {
                context.startActivity(intent);
            } else {
                MCLoggerFactory.getLogger().info("Can't load location");
                Toast.makeText(context, context.getString(R.string.mx_msg_error_no_current_gps_location), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            MCLoggerFactory.getLogger().error("Stub!!", e);
            Toast.makeText(context, context.getString(R.string.mx_msg_error_with_start_gps), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public static boolean showTomTomOrDefaultNavigator(final Address address, final Context context) {
        try {
            MXNavApp app = MxApplication.getInstance().getMxNavApp();
            if (app != null) {
                app.planTrip(address.getLatitude(), address.getLongitude(),
                        new Trip.PlanListener() {

                            public void onTripPlanResult(Trip trip, Trip.PlanResult planResult) {
                                onTripResult(trip, planResult);
                            }
                        });
                context.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                return true;
            } else {
                return showGoogleOrYandexNavigator(address, context);
            }
        } catch (Exception ignore) {
            return showGoogleOrYandexNavigator(address, context);
        }
    }

    protected static void onTripResult(final Trip trip, final Trip.PlanResult result) {
        //TODO imp if need it
        if (trip != null && result != null) {
            MCLoggerFactory.getLogger(MxAndroidUtil.class).debug("Trip: " + trip + "\n Result: " + result);
        }
    }

    /**
     * Return Intent for starting navigator from current location to Address location. Address must have Lat and Lon.
     * This method can't start GoogleNavigator.
     */
    public static Intent startGPSNavigator(final Address address) throws NullPointerException {
        LocationEntity myLocation = getGeoLocation();
        if (myLocation == null) {
            return null;
        }
//*
        // Allow start in other browsers //
        String refHTML = String.format(
                "http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s",
                myLocation.getLat(), myLocation.getLon(),
                address.getLatitude(), address.getLongitude());

        Uri uri = Uri.parse(refHTML);
        // Don't show browsers in App chosen dialog//
/*/
        String refGEO = String.format("geo:%s,%s?q=%s,%s",
                myLocation.getLat(), myLocation.getLon(),
                address.getLatitude(), address.getLongitude());
        Uri uri = Uri.parse(refGEO);
//*/
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    @SuppressWarnings("unused")
    //Use this for clear preference for default application//
    public static Intent clearPreference(final Context context, final Intent intent) throws SecurityException {
        PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
        IntentFilter intentFilter = new IntentFilter(intent.getAction());
        if (intent.getCategories() != null) {
            for (String category : intent.getCategories()) {
                intentFilter.addCategory(category);
            }
        }
        List<PackageInfo> packageInfoList = new LinkedList<PackageInfo>();
        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(intentFilter);
        List<ComponentName> preferredActivities = new ArrayList<ComponentName>();
        pm.getPreferredActivities(filters, preferredActivities, null);
        for (ComponentName componentName : preferredActivities) {
            for (ResolveInfo resolveInfo : resolveInfoList) {
                if (resolveInfo.activityInfo.applicationInfo.packageName.equals(componentName.getPackageName())) {
                    try {
                        packageInfoList.add(pm.getPackageInfo(componentName.getPackageName(), 0));
                    } catch (PackageManager.NameNotFoundException e) {
                        MCLoggerFactory.getLogger().error("Stub!!", e);
                    }
                }
            }
        }
        for (PackageInfo packageInfo : packageInfoList) {
            pm.clearPackagePreferredActivities(packageInfo.packageName);
        }
        return intent;
    }

    /**
     * Return Intent for starting Yandex Navigator from current location to Address location. Address must have Lat and Lon.
     */
    public static Intent launchYandexNavigator(final Context context, Address address) {
        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
        intent.setPackage("ru.yandex.yandexnavi");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() == 0) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
        } else {
            LocationEntity location = MxAndroidUtil.getGeoLocation();
            if (location != null) {
                intent.putExtra("lat_from", location.getLat());
                intent.putExtra("lon_from", location.getLon());
            }
            intent.putExtra("lat_to", address.getLatitude());
            intent.putExtra("lon_to", address.getLongitude());
        }
        return intent;
    }

    /**
     * Return Intent for starting Google Navigator from current location to Address location. Address must have Lat and Lon.
     */
    public static Intent launchGoogleNavigator(final Address address) {
        Uri uri = Uri.parse(String.format("google.navigation:q=%s,%s", address.getLatitude(), address.getLongitude()));
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static LocationEntity getGeoLocation() {
        MXNavApp mxNavApp = MxApplication.getInstance().getMxNavApp();
        LocationEntity location = null;
        if (mxNavApp != null) {
            location = mxNavApp.getLocation(5 * 60 * 1000); //5 minutes Turkish
        }
        if (location == null) {
            LocationService service = ServicesRegistry.getLocationService();
            if (service != null) {
                Location l = service.getLocation();
                if (l != null) {
                    location = new LocationEntity();
                    location.setDate(l.getTime());
                    location.setLat(l.getLatitude());
                    location.setLon(l.getLongitude());
                    location.setSpeed(l.getSpeed());
                    location.setUserId(Settings.get().getUserId());
                }
            } else {
                MCLoggerFactory.getLogger("Location service not bound.");
            }
        }
        if (location == null) {
            MCLoggerFactory.getLogger(MxAndroidUtil.class).info("Current location not found.");
        }
        return location;
    }

    public static String readResponse(InputStream in) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    static class DeprecatedUtils extends AndroidUtil2 {

        DeprecatedUtils(Context applicationContext) {
            super(applicationContext);
            ReflectionUtils.setValueOfPrivateField(AndroidUtil2.class, this, "gpsLocationListener", new EmptyLocationListener());
            ReflectionUtils.setValueOfPrivateField(AndroidUtil2.class, this, "networkLocationListener", new EmptyLocationListener());
        }

        public GeoLocation getGeoLocation(final int locationMaxAge) {
            return null;
        }

        public void initLocationAPI() {
        }

        public void shutdownLocationAPI() {
        }
    }

    private static class EmptyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }
}