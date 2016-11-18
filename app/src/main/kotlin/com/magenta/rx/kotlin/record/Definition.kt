package com.magenta.rx.kotlin.record

class Definition(private var text: String?, private var pos: String?, private var ts: String?, private var tr: Array<Transcription>?) {

    fun getText() = text

    fun getPos() = pos

    fun getTs() = ts

    fun getTr() = tr

    fun setText(text: String) {
        this.text = text
    }

    fun setPos(pos: String) {
        this.pos = pos
    }

    fun setTs(ts: String) {
        this.ts = ts
    }

    fun setTr(tr: Array<Transcription>) {
        this.tr = tr
    }
}