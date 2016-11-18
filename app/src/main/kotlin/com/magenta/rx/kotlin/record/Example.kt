package com.magenta.rx.kotlin.record

class Example(private var text: String?, private var tr: Array<Transcription>?) {

    fun getText() = text

    fun getTr() = tr

    fun setText(text: String) {
        this.text = text
    }

    fun setTr(tr: Array<Transcription>) {
        this.tr = tr
    }
}