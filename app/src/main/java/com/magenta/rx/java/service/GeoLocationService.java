package com.magenta.rx.java.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.binder.LocalBinder;

import rx.subjects.PublishSubject;

public class GeoLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private IBinder binder;
    private GoogleApiClient client;
    private LocationRequest request;
    private PublishSubject<Location> publisher;

    public void onCreate() {
        super.onCreate();
        publisher = PublishSubject.create();
        request = new LocationRequest().setInterval(120000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setFastestInterval(30000);
        client = new GoogleApiClient.Builder(RXApplication.getInstance(), this, this).addApi(LocationServices.API).build();
        client.connect();
        binder = new LocalBinder<>(this);
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    public PublishSubject<Location> getLocations() {
        return publisher;
    }

    @SuppressWarnings("MissingPermission")
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    public void onConnectionSuspended(int i) {
        Log.d(getClass().getName(), "onConnectionSuspended");
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(getClass().getName(), "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    public void onLocationChanged(Location location) {
        publisher.onNext(location);
    }
}