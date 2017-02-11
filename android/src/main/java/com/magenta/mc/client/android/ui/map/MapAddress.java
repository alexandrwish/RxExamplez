package com.magenta.mc.client.android.ui.map;

import com.magenta.maxunits.mobile.entity.Address;

import java.io.Serializable;

public class MapAddress implements Serializable {

    private static final long serialVersionUID = -5756088905684218456L;

    private final String full;
    private final String postal;
    private final Double latitude;
    private final Double longitude;

    private MapAddress(final String full, final String postal, final Double latitude, final Double longitude) {
        this.full = full;
        this.postal = postal;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static MapAddress from(final Address address) {
        return address != null ? new MapAddress(address.getFullAddress(), address.getPostal(), address.getLatitude(), address.getLongitude()) : null;
    }

    public String getFull() {
        return full;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPostal() {
        return postal;
    }
}