package com.magenta.rx.kotlin.record

class RowResult constructor(private var x: Double, private var fx: Double, private var asin: Double, private var sinF: Double) {

    fun getX() = x
    fun getFx() = fx
    fun getAsin() = asin
    fun getSinF() = sinF
}