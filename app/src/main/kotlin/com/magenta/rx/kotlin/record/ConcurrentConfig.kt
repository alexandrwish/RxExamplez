package com.magenta.rx.kotlin.record

import javax.inject.Inject

class ConcurrentConfig @Inject constructor() {

    private var xn: Int? = null
    private var xk: Int? = null
    private var dx: Int? = null
    private var eps: Int? = null

    fun getXn() = xn ?: 0

    fun getXk() = xk ?: 0

    fun getDx() = dx ?: 0

    fun getEps() = eps ?: 0

    fun setXn(xn: Int?) {
        this.xn = xn
    }

    fun setXk(xk: Int) {
        this.xk = xk
    }

    fun setDx(dx: Int) {
        this.dx = dx
    }

    fun setEps(eps: Int) {
        this.eps = eps
    }

    override fun toString(): String {
        return " " + getXn() + " " + getXk() + " " + getDx() + " " + getEps()
    }
}