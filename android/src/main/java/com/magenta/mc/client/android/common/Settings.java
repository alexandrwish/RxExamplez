package com.magenta.mc.client.android.common;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.magenta.mc.client.android.McAndroidApplication;

@SuppressWarnings("unused")
public class Settings {

    private static final Settings instance = new Settings();

    private static final String FACT_COST = "fact.cost";
    private static final String BARCODE_SCREEN = "barcode.screen";
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
        return "";
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

        public SettingsBuilder setAuthToken(String token) {
            if (token != null) {
                editor.putString(AUTH_TOKEN, token);
            }
            return this;
        }

        public void apply() {
            editor.apply();
        }
    }
}