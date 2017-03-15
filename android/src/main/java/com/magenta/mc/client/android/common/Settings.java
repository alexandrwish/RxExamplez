package com.magenta.mc.client.android.common;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.ui.theme.Theme;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class Settings {

    public static final String USER_LOCALE = "user.locale";
    public static final String APP_THEME = "app.theme";
    public static final String SETTING_PASSWORD = "settings.password";

    private static final Settings instance = new Settings();
    private static final String FACT_COST = "fact.cost";
    private static final String BARCODE_SCREEN = "barcode.screen";
    private static final String UPDATE_ALERT = "update.alert";
    private static final String SIGNATURE_SCREEN = "signature.screen";
    private static final String RANDOM_ORDERS = "random.orders";
    private static final String SEVERAL_RUNS = "several.runs";
    private static final String DISPLAY_MAP = "display.map";
    private static final String SHOW_ALERT = "show.alert";
    private static final String NON_COMPLETED = "non.completed";
    private static final String ALERT_DELAY = "alert.delay";
    private static final String DISPATCHER_PHONE = "dispatcher.phone";
    private static final String CAPACITY_UNIT = "capacity.unit";
    private static final String VOLUME_UNIT = "volume.unit";
    private static final String DEFAULT_MAP = "default.map";
    private static final String AUTH_TOKEN = "api.key";
    private static final String USER_LOGIN = "user.login";
    private static final String USER_ACCOUNT = "user.account";
    private static final String APP_HOST = "app.host";
    private static final String APP_PORT = "app.port";
    private static final String AUDIO_ALERT = "audio.alert";
    private static final String CANCEL_REASONS = "cancel.reasons";
    private static final String MOTO_BARCODE = "moto.barcode";
    private static final String SAVE_LOCATION_INTERVAL = "save.location.interval";
    private static final String TRACKING_ENABLED = "tracking.enabled";
    private static final String TIMEZONE = "timezone";
    private static final String SENTRY_DSN = "sentry.dsn";
    public static String[] IGNORED_MAP_PROVIDERS = {
            "googlesatellite",
            "ptv",
            "geoinformsputnik",
            "geobase",
            "geoserver",
            "yahoo",
            "mapquest",
            "map24"
    };
    private final SharedPreferences preferences;

    private Settings() {
        preferences = PreferenceManager.getDefaultSharedPreferences(McAndroidApplication.getInstance());
    }

    public static Settings get() {
        return instance;
    }

    public Boolean getFactCost() {
        return preferences.getBoolean(FACT_COST, false);
    }

    public Boolean getBarcodeScreen() {
        return preferences.getBoolean(BARCODE_SCREEN, false);
    }

    public Boolean getSignatureScreen() {
        return preferences.getBoolean(SIGNATURE_SCREEN, false);
    }

    public Boolean getRandomOrders() {
        return preferences.getBoolean(RANDOM_ORDERS, false);
    }

    public Boolean getSeveralRuns() {
        return preferences.getBoolean(SEVERAL_RUNS, false);
    }

    public Boolean getDisplayMap() {
        return preferences.getBoolean(DISPLAY_MAP, false);
    }

    public Boolean getShowAlert() {
        return preferences.getBoolean(SHOW_ALERT, false);
    }

    public Integer getNonCompleted() {
        return preferences.getInt(NON_COMPLETED, 0);
    }

    public Long getAlertDelay() {
        return preferences.getLong(ALERT_DELAY, 0L);
    }

    public String getDispatcherPhone() {
        return preferences.getString(DISPATCHER_PHONE, "");
    }

    public String getCapacityUnit() {
        return preferences.getString(CAPACITY_UNIT, "");
    }

    public String getVolumeUnit() {
        return preferences.getString(VOLUME_UNIT, "");
    }

    public String getDefaultMap() {
        return preferences.getString(DEFAULT_MAP, "");
    }

    public String getAuthToken() {
        return preferences.getString(AUTH_TOKEN, "");
    }

    public String getLogin() {
        return preferences.getString(USER_LOGIN, "");
    }

    public String getAccount() {
        return preferences.getString(USER_ACCOUNT, "");
    }

    public String getUserId() {
        return getAccount() + "-" + getLogin();
    }

    public String getLocale() {
        return preferences.getString(USER_LOCALE, "");
    }

    public String getHost() {
        return preferences.getString(APP_HOST, "");
    }

    public String getPort() {
        return preferences.getString(APP_PORT, "");
    }

    public Boolean getAudioAlert() {
        return preferences.getBoolean(AUDIO_ALERT, false);
    }

    public Set<String> getCancelReasons() {
        return preferences.getStringSet(CANCEL_REASONS, new HashSet<String>());
    }

    public Boolean getMotoBarcode() {
        return preferences.getBoolean(MOTO_BARCODE, false);
    }

    public Long getSaveLocationInterval() {
        return preferences.getLong(SAVE_LOCATION_INTERVAL, 60000L);
    }

    public Boolean getTrackingEnabled() {
        return preferences.getBoolean(TRACKING_ENABLED, true);
    }

    public String getTimezone() {
        return preferences.getString(TIMEZONE, "DEFAULT");
    }

    public String getSentryURL() {
        return preferences.getString(SENTRY_DSN, "");
    }

    public Theme getTheme() {
        return Theme.lookup(Integer.valueOf(preferences.getString(APP_THEME, "1")));
    }

    public static class SettingsBuilder {

        private static final SettingsBuilder instance = new SettingsBuilder();

        private SharedPreferences.Editor editor;

        private SettingsBuilder() {
        }

        public static SettingsBuilder get() {
            return instance;
        }

        @SuppressLint("CommitPrefEdits")
        public SettingsBuilder start() {
            editor = Settings.get().preferences.edit();
            return this;
        }

        public SettingsBuilder setFactCost(Boolean factCost) {
            if (factCost != null) {
                editor.putBoolean(FACT_COST, factCost);
            }
            return this;
        }

        public SettingsBuilder setBarcodeScreen(Boolean barcodeScreen) {
            if (barcodeScreen != null) {
                editor.putBoolean(BARCODE_SCREEN, barcodeScreen);
            }
            return this;
        }

        public SettingsBuilder setSignatureScreen(Boolean signatureScreen) {
            if (signatureScreen != null) {
                editor.putBoolean(SIGNATURE_SCREEN, signatureScreen);
            }
            return this;
        }

        public SettingsBuilder setRandomOrders(Boolean randomOrders) {
            if (randomOrders != null) {
                editor.putBoolean(RANDOM_ORDERS, randomOrders);
            }
            return this;
        }

        public SettingsBuilder setSeveralRuns(Boolean severalRuns) {
            if (severalRuns != null) {
                editor.putBoolean(SEVERAL_RUNS, severalRuns);
            }
            return this;
        }

        public SettingsBuilder setDisplayMap(Boolean enableMap) {
            if (enableMap != null) {
                editor.putBoolean(DISPLAY_MAP, enableMap);
            }
            return this;
        }

        public SettingsBuilder setShowAlert(Boolean enableAlert) {
            if (enableAlert != null) {
                editor.putBoolean(SHOW_ALERT, enableAlert);
            }
            return this;
        }

        public SettingsBuilder setNonCompleted(Integer nonCompleted) {
            if (nonCompleted != null) {
                editor.putInt(NON_COMPLETED, nonCompleted);
            }
            return this;
        }

        public SettingsBuilder setAlertDelay(Long alertDelay) {
            if (alertDelay != null) {
                editor.putLong(ALERT_DELAY, alertDelay);
            }
            return this;
        }

        public SettingsBuilder setDispatcherPhone(String dispatcherPhone) {
            if (dispatcherPhone != null) {
                editor.putString(DISPATCHER_PHONE, dispatcherPhone);
            }
            return this;
        }

        public SettingsBuilder setCapacityUnit(String capacityUnit) {
            if (capacityUnit != null) {
                editor.putString(CAPACITY_UNIT, capacityUnit);
            }
            return this;
        }

        public SettingsBuilder setVolumeUnit(String volumeUnit) {
            if (volumeUnit != null) {
                editor.putString(VOLUME_UNIT, volumeUnit);
            }
            return this;
        }

        public SettingsBuilder setDefaultMap(String defaultMap) {
            if (defaultMap != null) {
                editor.putString(DEFAULT_MAP, defaultMap);
            }
            return this;
        }

        public SettingsBuilder setLogin(String login) {
            if (login != null) {
                editor.putString(USER_LOGIN, login);
            }
            return this;
        }

        public SettingsBuilder setAccount(String account) {
            if (account != null) {
                editor.putString(USER_ACCOUNT, account);
            }
            return this;
        }

        public SettingsBuilder setLocale(String locale) {
            if (locale != null) {
                editor.putString(USER_LOCALE, locale);
            }
            return this;
        }

        public SettingsBuilder setAuthToken(String token) {
            if (token != null) {
                editor.putString(AUTH_TOKEN, token);
            }
            return this;
        }

        public SettingsBuilder setHost(String host) {
            if (host != null) {
                editor.putString(APP_HOST, host);
            }
            return this;
        }

        public SettingsBuilder setPort(String port) {
            if (port != null) {
                editor.putString(APP_PORT, port);
            }
            return this;
        }

        public SettingsBuilder setAudioAlert(Boolean audioAlert) {
            if (audioAlert != null) {
                editor.putBoolean(AUDIO_ALERT, audioAlert);
            }
            return this;
        }

        public SettingsBuilder setOrderCancellationReasons(Set<String> reasons) {
            if (reasons != null) {
                editor.putStringSet(CANCEL_REASONS, reasons);
            }
            return this;
        }

        public SettingsBuilder setMotoBarcode(Boolean motoBarcode) {
            if (motoBarcode != null) {
                editor.putBoolean(MOTO_BARCODE, motoBarcode);
            }
            return this;
        }

        public SettingsBuilder setSaveLocationInterval(Long saveLocationInterval) {
            if (saveLocationInterval != null) {
                editor.putLong(SAVE_LOCATION_INTERVAL, saveLocationInterval);
            }
            return this;
        }

        public SettingsBuilder setTrackingEnabled(Boolean trackingEnabled) {
            if (trackingEnabled != null) {
                editor.putBoolean(TRACKING_ENABLED, trackingEnabled);
            }
            return this;
        }

        public SettingsBuilder setTimezone(String timezone) {
            if (timezone != null) {
                editor.putString(TIMEZONE, timezone);
            }
            return this;
        }

        public SettingsBuilder setTheme(String theme) {
            if (theme != null) {
                editor.putString(APP_THEME, theme);
            }
            return this;
        }

        public void apply() {
            editor.apply();
        }
    }
}