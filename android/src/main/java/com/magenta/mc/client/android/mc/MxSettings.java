package com.magenta.mc.client.android.mc;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.settings.AndroidSettings;
import com.magenta.mc.client.android.util.Checksum;
import com.magenta.mc.client.android.util.DemoDataUtils;
import com.magenta.mc.client.android.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Deprecated
// TODO: 2/15/17 use shared preferences
public class MxSettings extends AndroidSettings {

    public static final String SETTING_PASSWORD = "settings.password";
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

    private static final String ALLOW_TO_PASS_JOBS_IN_ARBITRARY_ORDER = "allowToPassJobsInArbitraryOrder";
    private static final String ALLOW_TO_PASS_STOPS_IN_ARBITRARY_ORDER = "allowToPassStopsInArbitraryOrder";
    private static final String IGNORE_NEW_RUN_DUPLICATES = "ignoreNewRunDuplicates";
    private static final String USE_COMPONENT_NAME_AS_USER_PREFIX = "useComponentNameAsUserPrefix";
    private static final String SIGNATURE_ENABLED = "com.magenta.maxunits.mobile.maxunites.signature.enabled";
    private static final String BARCODE_ENABLED = "com.magenta.maxunits.mobile.maxunites.barcode.enabled";
    private static final String FACT_COST_ENABLED = "com.magenta.maxunits.mobile.maxunites.fact.cost.enabled";
    private static final String MAP_TRACKING_ENABLED = "map.tracking.enabled";
    private static final String INCOMING_UPDATE_PLAY_SOUND = "incoming-update.play-sound";
    private static final String FEATURES_ACCOUNT = "features.account";
    private static final String FEATURES_ACCOUNT_CONFIGURATION = "features.account-configuration";
    private static final String DISPATCHER_PHONE_NUMBER = "dispatcher.phone";
    private static final String DELETE_JOBS_OLDER = "com.magenta.maxunits.mobile.maxunites.time.period.of.non.completed";
    private static final String ORDER_CANCEL_REASONS = "orderCancelReasons";
    private static final String SAVE_LOCATIONS_INTERVAL = "save.locations.interval";
    private static final String ENAMBLE_MAP_DISPLAYING = "com.magenta.maxunits.mobile.maxunites.map.enabled";
    private static final String USER_ACCOUNT = "user.account";
    private static final String UPDATE_DELAYED = "update_already.delayed";



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

    public boolean isMapTrackingEnabled() {
        return getBooleanProperty(MAP_TRACKING_ENABLED, "true");
    }

    public void setMapTrackingEnabled(final boolean mapTrackingEnabled) {
        setProperty(MAP_TRACKING_ENABLED, Boolean.toString(mapTrackingEnabled));
    }

    public boolean isAllowToPassJobsInArbitraryOrder() {
        return getBooleanProperty(ALLOW_TO_PASS_JOBS_IN_ARBITRARY_ORDER, "true");
    }

    public boolean isAllowToPassStopsInArbitraryOrder() {
        return getBooleanProperty(ALLOW_TO_PASS_STOPS_IN_ARBITRARY_ORDER, "true");
    }

    public boolean isIgnoreNewRunDuplicates() {
        return getBooleanProperty(IGNORE_NEW_RUN_DUPLICATES, "true");
    }

    public boolean isUseComponentNameAsUserPrefix() {
        return getBooleanProperty(USE_COMPONENT_NAME_AS_USER_PREFIX, "true");
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

    public String getLocale() {
        return getProperty(Settings.LOCALE_KEY, "en");
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

    public void setOrderCancelReasons(List<String> reasons) {
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