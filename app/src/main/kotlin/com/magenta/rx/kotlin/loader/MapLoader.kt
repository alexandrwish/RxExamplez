package com.magenta.rx.kotlin.loader

import java.util.*
import javax.inject.Inject

class MapLoader @Inject constructor() {

    private val random = Random()
    private val max = 900
    private val min = -900

    fun getX() = (random.nextInt(max - min + 1) + min) / 10.0
    fun getY() = (random.nextInt(max - min + 1) + min) / 10.0
}