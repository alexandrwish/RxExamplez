package com.magenta.rx.kotlin.record

class Example {

    private var text: String? = null
    private var tr: Array<Transcription>? = null

    constructor(text: String?, tr: Array<Transcription>?) {
        this.text = text
        this.tr = tr
    }

    constructor()

    fun getText() = text

    fun setText(text: String) {
        this.text = text
    }

    fun getTr() = tr

    fun setTr(tr: Array<Transcription>) {
        this.tr = tr
    }
}