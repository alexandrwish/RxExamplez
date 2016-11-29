package com.magenta.rx.kotlin.loader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.model.entity.GeoLocationEntity
import com.magenta.rx.kotlin.binder.LocalBinder
import com.magenta.rx.kotlin.presenter.ServicePresenter
import com.magenta.rx.kotlin.service.LocationService
import org.greenrobot.greendao.rx.RxDao
import rx.Observable
import javax.inject.Inject

class ServiceLoader @Inject constructor() {

    private lateinit var listener: ServicePresenter.LocationListener

    fun setLocationListener(listener: ServicePresenter.LocationListener) {
        this.listener = listener
    }

    init {
        val context = RXApplication.getInstance()
        context.bindService(Intent(context, LocationService::class.java), object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                listener.load(Observable.merge(
                        ((service as LocalBinder<*>).service as LocationService).locations
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
}