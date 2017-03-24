package com.magenta.mc.client.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.magenta.mc.client.android.storage.FieldGetter;
import com.magenta.mc.client.android.storage.FieldSetter;
import com.magenta.mc.client.android.storage.StorableField;

public class Address extends StorableField implements Parcelable {

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    private static final long serialVersionUID = 3;
    private static final String FIELD_FULL_ADDRESS = "full.address";
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

    protected Address(Parcel in) {
        fullAddress = in.readString();
        postal = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
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
                new FieldSetter(FIELD_FULL_ADDRESS) {
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
                new FieldGetter(FIELD_FULL_ADDRESS) {
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullAddress);
        dest.writeString(postal);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}