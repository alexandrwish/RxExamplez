package com.magenta.mc.client.android.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.mc.client.android.record.LocationRecord;

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

    public LocationRecord toRecord() {
        return new LocationRecord(this);
    }
}