package com.magenta.rx.kotlin.record

import javax.inject.Inject

class ConcurrentConfig @Inject constructor() {

    private var xn: Double? = null
    private var xk: Double? = null
    private var dx: Double? = null
    private var eps: Double? = null

    fun getXn() = xn ?: 0.0

    fun getXk() = xk ?: 0.0

    fun getDx() = dx ?: 0.0

    fun getEps() = eps ?: 0.0

    fun setXn(xn: Double) {
        this.xn = xn
    }

    fun setXk(xk: Double) {
        this.xk = xk
    }

    fun setDx(dx: Double) {
        this.dx = dx
    }

    fun setEps(eps: Double) {
        this.eps = eps
    }
}