package com.magenta.rx.kotlin.loader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.binder.LocalBinder
import com.magenta.rx.java.model.entity.GeoLocationEntity
import com.magenta.rx.java.presenter.ServicePresenter
import com.magenta.rx.java.service.GeoLocationService
import org.greenrobot.greendao.rx.RxDao
import rx.Observable

class ServiceLoader {

    private lateinit var listener: ServicePresenter.LocationListener

    constructor() {
        val context = RXApplication.getInstance()
        context.bindService(Intent(context, GeoLocationService::class.java), object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                listener.load(Observable.merge(
                        ((service as LocalBinder<*>).service as GeoLocationService).locations
                                .doOnNext { location -> RXApplication.getInstance().session.geoLocationEntityDao.insert(GeoLocationEntity(null, location.latitude, location.longitude, location.time)) },
                        RxDao(RXApplication.getInstance().session.geoLocationEntityDao).loadAll()
                                .flatMap { geoLocationEntities -> Observable.from(geoLocationEntities) }
                                .map { geoLocationEntity ->
                                    val location = Location("gps")
                                    location.latitude = geoLocationEntity.lat
                                    location.longitude = geoLocationEntity.lon
                                    location.time = geoLocationEntity.timestamp
                                    location
                                })!!)
            }

            override fun onServiceDisconnected(className: ComponentName) {
            }
        }, Context.BIND_AUTO_CREATE)
    }

    fun setLocationListener(listener: ServicePresenter.LocationListener) {
        this.listener = listener
    }
}