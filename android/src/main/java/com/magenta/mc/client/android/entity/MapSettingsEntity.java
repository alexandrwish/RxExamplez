package com.magenta.mc.client.android.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.mc.client.android.entity.type.MapProviderType;

@DatabaseTable(tableName = "map_settings")
public class MapSettingsEntity extends AbstractEntity {

    @DatabaseField(columnDefinition = "driver")
    private String driver;
    @DatabaseField(columnDefinition = "settings")
    private String settings;
    @DatabaseField(columnDefinition = "map_provider_type", dataType = DataType.ENUM_STRING)
    private MapProviderType mapProviderType;
    @DatabaseField(columnDefinition = "provider")
    private String provider;
    @DatabaseField(columnDefinition = "remember")
    private boolean remember;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public MapProviderType getMapProviderType() {
        return mapProviderType;
    }

    public void setMapProviderType(MapProviderType mapProviderType) {
        this.mapProviderType = mapProviderType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    @Deprecated
    public Object toRecord() {
        return null;
    }
}