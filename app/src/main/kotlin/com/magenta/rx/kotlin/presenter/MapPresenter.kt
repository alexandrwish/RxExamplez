package com.magenta.rx.kotlin.presenter

import com.magenta.rx.kotlin.event.CleanEvent
import com.magenta.rx.kotlin.event.DrawMapEvent
import com.magenta.rx.kotlin.loader.MapLoader
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MapPresenter @Inject constructor(private val loader: MapLoader) {

    fun draw() {
        EventBus.getDefault().postSticky(DrawMapEvent(loader.getX(), loader.getY()))
    }

    fun clear() {
        EventBus.getDefault().postSticky(CleanEvent())
    }
}