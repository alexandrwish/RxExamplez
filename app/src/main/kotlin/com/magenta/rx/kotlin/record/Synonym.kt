package com.magenta.rx.kotlin.record

class Synonym {

    private var text: String? = null

    constructor(text: String?) {
        this.text = text
    }

    constructor()

    fun getText() = text

    fun setText(text: String) {
        this.text = text
    }
}