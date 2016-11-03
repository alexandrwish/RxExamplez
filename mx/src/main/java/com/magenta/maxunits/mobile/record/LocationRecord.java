package com.magenta.maxunits.mobile.record;

import com.magenta.maxunits.mobile.entity.LocationEntity;
import com.magenta.maxunits.mobile.mc.MxAndroidUtil;

public class LocationRecord {

    String imei;
    String login;
    double speed;
    double latitude;
    double longitude;
    long timestamp;

    public LocationRecord(LocationEntity entity) {
        timestamp = entity.getDate();
        login = entity.getUserId();
        latitude = entity.getLat();
        longitude = entity.getLon();
        speed = entity.getSpeed();
        imei = MxAndroidUtil.getImei();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timestamp = timeStamp;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}