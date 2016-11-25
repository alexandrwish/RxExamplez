package com.magenta.rx.kotlin.record

import javax.inject.Inject

class LazyConfig @Inject constructor() {

    var current: Int = 0
    var max: Int = 0
    var step: Int = 1
}