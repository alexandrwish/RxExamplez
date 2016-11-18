package com.magenta.rx.kotlin.record

class Synonym(private var text: String?) {

    fun getText() = text

    fun setText(text: String) {
        this.text = text
    }
}