package com.magenta.maxunits.mobile.dlib.mc;

public final class HDSettings {

    public static final String UI_ENABLE_MAP = "ui.enable.map";
    public static final String UI_MAP_PROVIDER = "ui.map.provider";
    public static final String MX_CONFIG_VOLUME_UNIT = "mx.config.volumeUnits";
    public static final String MX_CONFIG_CAPACITY_UNITS = "mx.config.capacity.units";

    public static boolean isDisableMap() {
        return !MxSettings.get().getBooleanProperty(UI_ENABLE_MAP, "false");
    }

    public static boolean isUseMaplet() {
        String tmp = MxSettings.get().getProperty(UI_MAP_PROVIDER);
        return tmp != null && tmp.equalsIgnoreCase("Leaflet");
    }

    public static boolean isUseYandex() {
        String tmp = MxSettings.get().getProperty(UI_MAP_PROVIDER);
        return tmp != null && tmp.equalsIgnoreCase("Yandex");
    }

    public static boolean isUseGoogle() {
        String tmp = MxSettings.get().getProperty(UI_MAP_PROVIDER);
        return tmp != null && tmp.equalsIgnoreCase("Google");
    }
}