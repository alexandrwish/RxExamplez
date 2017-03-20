package com.magenta.maxunits.mobile.hd;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Settings;

public class HDDistributionApp extends McAndroidApplication {

    public void onCreate() {
        instance = this;
        Settings settings = Settings.get();
        Settings.SettingsBuilder builder = Settings.SettingsBuilder.get().start();
        if (!settings.contains(Settings.APP_HOST)) {
            builder.setHost(BuildConfig.APP_HOST);
        }
        if (!settings.contains(Settings.APP_LOCALE)) {
            builder.setLocale(BuildConfig.APP_LOCALE);
        }
        if (!settings.contains(Settings.APP_PORT)) {
            builder.setPort(BuildConfig.APP_PORT);
        }
        if (!settings.contains(Settings.APP_THEME)) {
            builder.setTheme(BuildConfig.APP_THEME);
        }
        if (!settings.contains(Settings.LOC_SAVE)) {
            builder.setLocationSave(Long.valueOf(BuildConfig.LOC_SAVE));
        }
        if (!settings.contains(Settings.LOC_ENABLE)) {
            builder.setLocationEnable(Boolean.valueOf(BuildConfig.LOC_ENABLE));
        }
        if (!settings.contains(Settings.SETTING_PASSWORD)) {
            builder.setSettingsPassword(BuildConfig.PASSWORD);
        }
        if (!settings.contains(Settings.UPDATE_ALERT)) {
            builder.setUpdateAlert(Boolean.valueOf(BuildConfig.UPDATE_ALERT));
        }
        if (!settings.contains(Settings.MOTO_BARCODE)) {
            builder.setMotoBarcode(Boolean.valueOf(BuildConfig.MOTO_BARCODE));
        }
        if (!settings.contains(Settings.CACHE_ENABLE)) {
            builder.setCacheEnable(Boolean.valueOf(BuildConfig.CACHE_ENABLE));
        }
        if (!settings.contains(Settings.CACHE_SPACE)) {
            builder.setCacheSpace(BuildConfig.CACHE_SPACE);
        }
        if (!settings.contains(Settings.CACHE_PERIOD)) {
            builder.setCachePeriod(BuildConfig.CACHE_PERIOD);
        }
        builder.apply();
        super.onCreate();
    }

    public String getName() {
        return "HD";
    }
}