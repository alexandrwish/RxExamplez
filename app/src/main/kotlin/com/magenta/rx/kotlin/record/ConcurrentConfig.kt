package com.magenta.rx.kotlin.record

import javax.inject.Inject

class ConcurrentConfig @Inject constructor() {

    private var start: Int? = null
    private var end: Int? = null
    private var step: Int? = null

    fun getStart() = start ?: 0

    fun getEnd() = end ?: 0

    fun getStep() = step ?: 0

    fun setStart(start: Int) {
        this.start = start
    }

    fun setEnd(end: Int) {
        this.end = end
    }

    fun setStep(step: Int) {
        this.step = step
    }
}