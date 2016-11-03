package com.magenta.maxunits.mobile.dlib.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.maxunits.mobile.dlib.record.PhoneStatisticRecord;

import java.util.Date;

@DatabaseTable(tableName = "phone_state")
public class PhoneStatisticEntity extends AbstractEntity<PhoneStatisticRecord> {

    @DatabaseField(columnName = "gps_state")
    private boolean gpsState;
    @DatabaseField(columnName = "gprs_state")
    private boolean gprsState;
    @DatabaseField(columnName = "battery_state")
    private float batteryState;
    @DatabaseField(columnName = "date", dataType = DataType.DATE_STRING)
    private Date date;

    public boolean isGpsState() {
        return gpsState;
    }

    public void setGpsState(boolean gpsState) {
        this.gpsState = gpsState;
    }

    public boolean isGprsState() {
        return gprsState;
    }

    public void setGprsState(boolean gprsState) {
        this.gprsState = gprsState;
    }

    public float getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(float batteryState) {
        this.batteryState = batteryState;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PhoneStatisticRecord toRecord() {
        return new PhoneStatisticRecord(date, gpsState, gprsState, batteryState);
    }
}