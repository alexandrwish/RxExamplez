package com.magenta.rx.kotlin.presenter

import android.location.Location
import com.magenta.rx.kotlin.event.ReceivedLocationEvent
import com.magenta.rx.kotlin.loader.ServiceLoader
import org.greenrobot.eventbus.EventBus
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject

class ServicePresenter @Inject constructor(loader: ServiceLoader) {

    init {
        loader.setLocationListener(object : LocationListener {
            override fun load(observable: Observable<Location>) {
                this@ServicePresenter.load(observable)
            }
        })
    }

    private fun load(observable: Observable<Location>) {
        observable.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe { location -> EventBus.getDefault().postSticky(ReceivedLocationEvent(location.latitude, location.longitude, location.time)) }
    }

    interface LocationListener {
        fun load(observable: Observable<Location>)
    }
}