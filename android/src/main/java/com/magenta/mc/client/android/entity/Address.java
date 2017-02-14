package com.magenta.mc.client.android.entity;

import com.magenta.mc.client.android.mc.storage.FieldGetter;
import com.magenta.mc.client.android.mc.storage.FieldSetter;
import com.magenta.mc.client.android.mc.storage.StorableField;

public class Address extends StorableField {

    private static final long serialVersionUID = 3;

    private static final String FIELD_FULLADDRESS = "fulladdress";
    private static final String FIELD_POSTAL = "postal";
    private static final String FIELD_LATITUDE = "latitude";
    private static final String FIELD_LONGITUDE = "longitude";

    private String fullAddress;
    private String postal;
    private Double latitude;
    private Double longitude;

    public Address(String fullAddress, String postal) {
        this.fullAddress = fullAddress;
        this.postal = postal;
    }

    public Address() {
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPostal() {
        return postal;
    }

    public String getRoutingAddress() {
        return postal;
    }

    public String toString() {
        return (fullAddress != null && !(fullAddress.length() == 0) ? fullAddress + "," : "") + postal;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    public FieldSetter[] getSetters() {
        return new FieldSetter[]{
                new FieldSetter(FIELD_FULLADDRESS) {
                    public void setValue(Object value) {
                        fullAddress = (String) value;
                    }
                },
                new FieldSetter(FIELD_POSTAL) {
                    public void setValue(Object value) {
                        postal = (String) value;
                    }
                },
                new FieldSetter(FIELD_LATITUDE) {
                    public void setValue(Object value) {
                        latitude = (Double) value;
                    }
                },
                new FieldSetter(FIELD_LONGITUDE) {
                    public void setValue(Object value) {
                        longitude = (Double) value;
                    }
                }
        };
    }

    public FieldGetter[] getGetters() {
        return new FieldGetter[]{
                new FieldGetter(FIELD_FULLADDRESS) {
                    public Object getValue() {
                        return fullAddress;
                    }
                },
                new FieldGetter(FIELD_POSTAL) {
                    public Object getValue() {
                        return postal;
                    }
                },
                new FieldGetter(FIELD_LATITUDE) {
                    public Object getValue() {
                        return latitude;
                    }
                },
                new FieldGetter(FIELD_LONGITUDE) {
                    public Object getValue() {
                        return longitude;
                    }
                }
        };
    }
}