package com.magenta.rx.kotlin.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.magenta.rx.java.RXApplication
import com.magenta.rx.kotlin.binder.LocalBinder
import rx.subjects.PublishSubject

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var binder: IBinder? = null
    private var client: GoogleApiClient? = null
    private lateinit var request: LocationRequest
    lateinit var locations: PublishSubject<Location>

    override fun onCreate() {
        super.onCreate()
        locations = PublishSubject.create<Location>()
        request = LocationRequest().setInterval(120000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setFastestInterval(30000)
        client = GoogleApiClient.Builder(RXApplication.getInstance(), this, this).addApi(LocationServices.API).build()
        client?.connect()
        binder = LocalBinder(this)
    }

    override fun onBind(intent: Intent) = binder

    override fun onConnected(bundle: Bundle?) {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this)
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(javaClass.name, "onConnectionSuspended")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(javaClass.name, "onConnectionFailed: " + connectionResult.errorMessage!!)
    }

    override fun onLocationChanged(location: Location) {
        locations.onNext(location)
    }
}