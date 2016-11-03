package com.magenta.maxunits.mobile.mc;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.magenta.maxunits.mobile.utils.Checksum;
import com.magenta.maxunits.mobile.utils.DemoDataUtils;
import com.magenta.maxunits.mobile.utils.StringUtils;
import com.magenta.mc.client.android.settings.AndroidSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public class MxSettings extends AndroidSettings {

    public static final String ALLOW_TO_PASS_JOBS_IN_ARBITRARY_ORDER = "allowToPassJobsInArbitraryOrder";
    public static final String ALLOW_TO_PASS_STOPS_IN_ARBITRARY_ORDER = "allowToPassStopsInArbitraryOrder";
    public static final String IGNORE_NEW_RUN_DUPLICATES = "ignoreNewRunDuplicates";
    public static final String USE_COMPONENT_NAME_AS_USER_PREFIX = "useComponentNameAsUserPrefix";
    public static final String SIGNATURE_ENABLED = "com.magenta.maxunits.mobile.maxunites.signature.enabled";
    public static final String BARCODE_ENABLED = "com.magenta.maxunits.mobile.maxunites.barcode.enabled";
    public static final String FACT_COST_ENABLED = "com.magenta.maxunits.mobile.maxunites.fact.cost.enabled";
    public static final String MAP_TRACKING_ENABLED = "map.tracking.enabled";
    public static final String INCOMING_UPDATE_PLAY_SOUND = "incoming-update.play-sound";
    public static final String FEATURES_ACCOUNT = "features.account";
    public static final String FEATURES_ACCOUNT_CONFIGURATION = "features.account-configuration";
    public static final String SETTING_PASSWORD = "settings.password";
    public static final String LOCALE = "locale.key";
    public static final String CONFIRM_ARRIVED_PERIOD = "confirmArrivedPeriod";
    public static final String COUNT_ALERTS_ON_ARRIVED = "countAlertsOnArrived";
    public static final String ALERT_DURATION = "alertDuration";
    public static final String COMPLETE_AREA_RADIUS = "completeAreaRadius";
    public static final String COUNT_ALERTS_ON_COMPLETE = "countAlertsOnComplete";
    public static final String PICKUP_AREA_RADIUS = "pickupAreaRadius";
    public static final String GEO_FENCE_ENABLE = "geoFenceEnable";
    public static final String DISPATCHER_PHONE_NUMBER = "dispatcher.phone";
    public static final String ALERT_ENABLE = "alert.enable";
    public static final String ALERT_DELAY = "alert.delay";
    public static final String CLEAN_CACHE_PERIOD = "clear.unused.cache";
    public static final String USED_CACHE_SPACE = "used.cache.space";
    public static final String ENABLE_CACHE = "enable.cache";
    public static final String MOTO_BARCODE = "barcode.scaner";
    public static final String ENABLE_API = "enable.api";
    public static final String API_PATH = "api.path";
    public static final String API_ADDRESS = "api.address";
    public static final String SENTRY_DSN = "sentry.dsn";
    public static final String DELETE_JOBS_OLDER = "com.magenta.maxunits.mobile.maxunites.time.period.of.non.completed";
    public static final String ORDER_CANCEL_REASONS = "orderCancelReasons";
    public static final String SAVE_LOCATIONS_INTERVAL = "save.locations.interval";
    public static final String ENAMBLE_MAP_DISPLAYING = "com.magenta.maxunits.mobile.maxunites.map.enabled";
    public static final String USER_ACCOUNT = "user.account";
    public static final String UPDATE_DELAYED = "update_already.delayed";
    public static String[] ignoredMapProviders = {
            "googlesatellite",
            "ptv",
            "geoinformsputnik",
            "geobase",
            "geoserver",
            "yahoo",
            "map24"
    };

    public MxSettings(Context applicationContext) {
        super(applicationContext);
    }

    public static MxSettings getInstance() {
        return (MxSettings) MxSettings.get();
    }

    public Object setProperty(final String key, String newValue) {
        // save only if changed
        if (key.equals(SETTING_PASSWORD)) {
            newValue = StringUtils.toBlank(newValue);
            final String oldValue = getSettingsPassword();
            if (oldValue == null ? newValue == null : oldValue.equals(newValue)) {
                newValue = oldValue;
            } else {
                newValue = newValue != null ? Checksum.md5(newValue) : "";
            }
        }
        return super.setProperty(key, newValue);
    }

    public String getUserAccount() {
        return getProperty(USER_ACCOUNT);
    }

    public void setUserAccount(String account) {
        setProperty(USER_ACCOUNT, account);
    }

    public long getSaveLocationInterval() {
        return getLongProperty(SAVE_LOCATIONS_INTERVAL, 50000);
    }

    public boolean isIncomingUpdatePlaySoundEnabled() {
        return getBooleanProperty(INCOMING_UPDATE_PLAY_SOUND, "true");
    }

    public void setIncomingUpdatePlaySoundEnabled(final boolean incomingUpdatePlaySoundEnabled) {
        setProperty(INCOMING_UPDATE_PLAY_SOUND, Boolean.toString(incomingUpdatePlaySoundEnabled));
    }

    public boolean isMapTrackingEnabled() {
        return getBooleanProperty(MAP_TRACKING_ENABLED, "true");
    }

    public void setMapTrackingEnabled(final boolean mapTrackingEnabled) {
        setProperty(MAP_TRACKING_ENABLED, Boolean.toString(mapTrackingEnabled));
    }

    public boolean isAllowToPassJobsInArbitraryOrder() {
        return getBooleanProperty(ALLOW_TO_PASS_JOBS_IN_ARBITRARY_ORDER, "true");
    }

    public void setAllowToPassJobsInArbitraryOrder(boolean allowToPassJobsInArbitraryOrder) {
        setProperty(ALLOW_TO_PASS_JOBS_IN_ARBITRARY_ORDER, Boolean.toString(allowToPassJobsInArbitraryOrder));
    }

    public boolean isAllowToPassStopsInArbitraryOrder() {
        return getBooleanProperty(ALLOW_TO_PASS_STOPS_IN_ARBITRARY_ORDER, "true");
    }

    public void setAllowToPassStopsInArbitraryOrder(boolean allowToPassJobsInArbitraryOrder) {
        setProperty(ALLOW_TO_PASS_STOPS_IN_ARBITRARY_ORDER, Boolean.toString(allowToPassJobsInArbitraryOrder));
    }

    public boolean isIgnoreNewRunDuplicates() {
        return getBooleanProperty(IGNORE_NEW_RUN_DUPLICATES, "true");
    }

    public void setIgnoreNewRunDuplicates(boolean ignoreNewRunDuplicates) {
        setProperty(IGNORE_NEW_RUN_DUPLICATES, Boolean.toString(ignoreNewRunDuplicates));
    }

    public boolean isUseComponentNameAsUserPrefix() {
        return getBooleanProperty(USE_COMPONENT_NAME_AS_USER_PREFIX, "true");
    }

    public void setUseComponentNameAsUserPrefix(boolean useComponentNameAsUserPrefix) {
        setProperty(USE_COMPONENT_NAME_AS_USER_PREFIX, Boolean.toString(useComponentNameAsUserPrefix));
    }

    public boolean isSignatureEnabled() {
        return getBooleanProperty(SIGNATURE_ENABLED, "false");
    }

    public void setSignatureEnabled(final boolean signatureEnabled) {
        setProperty(SIGNATURE_ENABLED, Boolean.toString(signatureEnabled));
    }

    public boolean isBarcodeEnabled() {
        return getBooleanProperty(BARCODE_ENABLED, "false");
    }

    public void setBarcodeEnabled(final boolean barcodeEnabled) {
        setProperty(BARCODE_ENABLED, Boolean.toString(barcodeEnabled));
    }

    public boolean isFactCostEnabled() {
        return getBooleanProperty(FACT_COST_ENABLED, "false");
    }

    public String getSettingsPassword() {
        return getProperty(SETTING_PASSWORD, null);
    }

    public void setSettingsPassword(final String password) {
        setProperty(SETTING_PASSWORD, password);
    }

    public DemoDataUtils.Country getDemoCountry() {
        return DemoDataUtils.parseCountryAndTown(getProperty("offline.demo.country", "Samara, RU")).first;
    }

    public DemoDataUtils.Town getDemoTown() {
        return DemoDataUtils.parseCountryAndTown(getProperty("offline.demo.country", "Samara, RU")).second;
    }

    public boolean hasFeature(final Features feature) {
        return getBooleanProperty(feature.getPropertyName(), "false");
    }

    public void enableFeature(final Features feature) {
        setProperty(feature.getPropertyName(), "true");
    }

    public void disableFeature(final Features feature) {
        setProperty(feature.getPropertyName(), "false");
    }

    public String getLocale() {
        return getProperty(LOCALE, "en");
    }

    public Integer getCompleteAreaRadius() {
        return getIntProperty(COMPLETE_AREA_RADIUS, "1");
    }

    public Integer getConfirmArrivedPeriod() {
        return getIntProperty(CONFIRM_ARRIVED_PERIOD, "1");
    }

    public Integer getCountAlertsOnArrived() {
        return getIntProperty(COUNT_ALERTS_ON_ARRIVED, "1");
    }

    public Integer getAlertDuration() {
        return getIntProperty(ALERT_DURATION, "1");
    }

    public Integer getCountAlertsOnComplete() {
        return getIntProperty(COUNT_ALERTS_ON_COMPLETE, "1");
    }

    public Integer getPickupAreaRadius() {
        return getIntProperty(PICKUP_AREA_RADIUS, "1");
    }

    public String getDispatcherPhoneNumber() {
        return getProperty(DISPATCHER_PHONE_NUMBER);
    }

    public Integer getDeleteJobsOlder() {
        return getIntProperty(DELETE_JOBS_OLDER, "1");
    }

    public ArrayList<String> getOrderCancelReasons() {
        return getList(ORDER_CANCEL_REASONS);
    }

    public void setOrderCancelReasons(ArrayList<String> reasons) {
        setList(ORDER_CANCEL_REASONS, reasons);
    }

    private <T> void setList(String key, List<T> values) {
        Gson gson = new Gson();
        setProperty(key, gson.toJson(values));
    }

    private <T> ArrayList<T> getList(String properties) {
        Gson gson = new Gson();
        ArrayList<T> result = gson.fromJson(getProperty(properties), new TypeToken<T>() {
        }.getType());
        return result == null || result.isEmpty() ? new ArrayList<T>() : result;
    }

    public boolean isMapDisplayingEnabled() {
        return getBooleanProperty(ENAMBLE_MAP_DISPLAYING, "true");
    }

    public boolean isUpdateDelayed() {
        return getBooleanProperty(UPDATE_DELAYED);
    }

    public void setUpdateDelayed(boolean isDelayed) {
        setProperty(UPDATE_DELAYED, Boolean.toString(isDelayed));
    }

    public enum Features {
        ACCOUNT(FEATURES_ACCOUNT),
        ACCOUNT_CONFIGURATION(FEATURES_ACCOUNT_CONFIGURATION);

        private final String propertyName;

        Features(final String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }
}