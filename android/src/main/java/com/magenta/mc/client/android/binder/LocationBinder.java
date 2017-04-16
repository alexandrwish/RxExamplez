package com.magenta.mc.client.android.binder;

import android.location.Location;
import android.os.Binder;

import com.magenta.mc.client.android.service.LocationService;

public class LocationBinder extends Binder {

    private final LocationService service;

    public LocationBinder(LocationService service) {
        this.service = service;
    }

    public Location getLocation() {
        return service.getLocation();
    }
}