package com.magenta.mc.client.android.record;

import java.io.Serializable;
import java.util.Date;

public class PhoneStatisticRecord implements Serializable {

    private Date date;
    private Boolean gps;
    private Boolean gprs;
    private Float battery;

    public PhoneStatisticRecord(Date date, boolean gps, boolean gprs, float battery) {
        this.date = date;
        this.gps = gps;
        this.gprs = gprs;
        this.battery = battery;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getGps() {
        return gps;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    public Boolean getGprs() {
        return gprs;
    }

    public void setGprs(Boolean gprs) {
        this.gprs = gprs;
    }

    public Float getBattery() {
        return battery;
    }

    public void setBattery(Float battery) {
        this.battery = battery;
    }
}