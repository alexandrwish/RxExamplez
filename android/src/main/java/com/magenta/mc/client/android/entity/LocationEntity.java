package com.magenta.mc.client.android.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "geo_locations")
public class LocationEntity {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private long id;
    @DatabaseField(columnName = "user_id", index = true)
    private String mUserId;
    @DatabaseField(columnName = "date", index = true)
    private long mDate;
    @DatabaseField(columnName = "lat")
    private Double mLat;
    @DatabaseField(columnName = "lon")
    private Double mLon;
    @DatabaseField(columnName = "speed")
    private Float mSpeed;
    @DatabaseField(columnName = "gps")
    private Boolean mGps;
    @DatabaseField(columnName = "gprs")
    private String mGprs;
    @DatabaseField(columnName = "battery")
    private Double mBattery;
    @DatabaseField(columnName = "token")
    private String mToken;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        mLat = lat;
    }

    public Double getLon() {
        return mLon;
    }

    public void setLon(Double lon) {
        mLon = lon;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public Float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(Float speed) {
        mSpeed = speed;
    }

    public Boolean getGps() {
        return mGps;
    }

    public void setGps(Boolean gps) {
        mGps = gps;
    }

    public String getGprs() {
        return mGprs;
    }

    public void setGprs(String gprs) {
        mGprs = gprs;
    }

    public Double getBattery() {
        return mBattery;
    }

    public void setBattery(Double battery) {
        mBattery = battery;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

}