package com.magenta.maxunits.mobile.dlib.record;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PhoneStatisticRecord {

    private static final String DATE = "date";
    private static final String GPS = "gps";
    private static final String GPRS = "gprs";
    private static final String BATTERY = "battery";
    @SerializedName(DATE)
    protected Date date;
    @SerializedName(GPS)
    protected Boolean gps;
    @SerializedName(GPRS)
    protected Boolean gprs;
    @SerializedName(BATTERY)
    protected Float battery;

    public PhoneStatisticRecord() {
        super();
    }

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