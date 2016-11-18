package com.magenta.rx.kotlin.record

class Transcription(private var text: String?, private var pos: String?, private var syn: Array<Synonym>?, private var mean: Array<Meaning>?, private var ex: Array<Example>?) {

    fun getText() = text

    fun getPos() = pos

    fun getSyn() = syn

    fun getMean() = mean

    fun getEx() = ex

    fun setText(text: String) {
        this.text = text
    }

    fun setPos(pos: String) {
        this.pos = pos
    }

    fun setSyn(syn: Array<Synonym>) {
        this.syn = syn
    }

    fun setMean(mean: Array<Meaning>) {
        this.mean = mean
    }

    fun setEx(ex: Array<Example>) {
        this.ex = ex
    }
}