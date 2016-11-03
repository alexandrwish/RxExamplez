package com.magenta.maxunits.mobile.entity;

public class VehicleCheck {

    private String callSign;
    private Integer mileage;
    private String dailyCheckDone;
    private String notes;
    private boolean enforceCheck;

    public VehicleCheck(String callSign, Integer mileage, String dailyCheckDone, String notes) {
        this.callSign = callSign;
        this.mileage = mileage;
        this.dailyCheckDone = dailyCheckDone;
        this.notes = notes;
    }

    public VehicleCheck(String callSign, Integer mileage, boolean enforceCheck) {
        this.callSign = callSign;
        this.mileage = mileage;
        this.enforceCheck = enforceCheck;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getDailyCheckDone() {
        return dailyCheckDone;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isEnforceCheck() {
        return enforceCheck;
    }

    public void setEnforceCheck(boolean enforceCheck) {
        this.enforceCheck = enforceCheck;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }
}