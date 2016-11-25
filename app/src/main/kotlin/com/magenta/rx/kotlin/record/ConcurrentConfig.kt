package com.magenta.rx.kotlin.record

import javax.inject.Inject

class ConcurrentConfig @Inject constructor() {

    var start: Int = 0
    var end: Int = 0
    var step: Int = 1
}