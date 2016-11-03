package com.magenta.maxunits.mobile.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.maxunits.mobile.record.LocationRecord;

@DatabaseTable(tableName = "geo_locations")
public class LocationEntity {

    @DatabaseField(id = false, generatedId = true, allowGeneratedIdInsert = true)
    long id;
    @DatabaseField(columnName = "user_id", index = true)
    String mUserId;
    @DatabaseField(columnName = "date", index = true)
    long mDate;
    @DatabaseField(columnName = "lat")
    Double mLat;
    @DatabaseField(columnName = "lon")
    Double mLon;
    @DatabaseField(columnName = "speed")
    Float mSpeed;

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