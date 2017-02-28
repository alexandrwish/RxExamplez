package com.magenta.mc.client.android.record;

import java.io.Serializable;

public class PointRecord implements Serializable {

    private Double longitude;
    private Double latitude;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String toString() {
        return "PointRecord{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}