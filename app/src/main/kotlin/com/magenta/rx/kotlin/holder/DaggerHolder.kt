package com.magenta.rx.kotlin.holder

import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.activity.*
import com.magenta.rx.java.component.*
import com.magenta.rx.java.module.*

class DaggerHolder(private val rxComponent: RXComponent) {

    private var mapComponent: MapComponent? = null
    private var serviceComponent: ServiceComponent? = null
    private var retrofitComponent: RetrofitComponent? = null
    private var dictionaryComponent: DictionaryComponent? = null
    private var concurrentComponent: ConcurrentComponent? = null

    init {
        rxComponent.inject(RXApplication.getInstance())
    }

    fun addRetrofitComponent(activity: RetrofitActivity) {
        if (retrofitComponent == null) {
            retrofitComponent = rxComponent.plusRetrofitComponent(RetrofitModule(activity))
        }
        retrofitComponent!!.inject(activity)
    }

    fun addMapComponent(activity: MapActivity) {
        if (mapComponent == null) {
            mapComponent = rxComponent.plusMapComponent(MapModule())
        }
        mapComponent!!.inject(activity)
    }

    fun addDictionaryComponent(activity: DictionaryActivity) {
        if (dictionaryComponent == null) {
            dictionaryComponent = rxComponent.plusDictionaryComponent(DictionaryModule(activity))
        }
        dictionaryComponent!!.inject(activity)
    }

    fun addServiceComponent(activity: ServiceActivity) {
        if (serviceComponent == null) {
            serviceComponent = rxComponent.plusServiceComponent(ServiceModule())
        }
        serviceComponent!!.inject(activity)
    }

    fun addConcurrentComponent(activity: ConcurrentActivity) {
        if (concurrentComponent == null) {
            concurrentComponent = rxComponent.plusConcurrentComponent(ConcurrentModule(activity))
        }
        concurrentComponent!!.inject(activity)
    }

    fun removeRetrofitComponent() {
        retrofitComponent = null
    }

    fun removeMapComponent() {
        mapComponent = null
    }

    fun removeDictionaryComponent() {
        dictionaryComponent = null
    }

    fun removeServiceComponent() {
        serviceComponent = null
    }

    fun removeConcurrentComponent() {
        concurrentComponent = null
    }
}