package com.magenta.rx.kotlin.record

class Transcription {

    private var text: String? = null
    private var pos: String? = null
    private var syn: Array<Synonym>? = null
    private var mean: Array<Meaning>? = null
    private var ex: Array<Example>? = null

    constructor(text: String?, pos: String?, syn: Array<Synonym>?, mean: Array<Meaning>?, ex: Array<Example>?) {
        this.text = text
        this.pos = pos
        this.syn = syn
        this.mean = mean
        this.ex = ex
    }

    constructor()

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