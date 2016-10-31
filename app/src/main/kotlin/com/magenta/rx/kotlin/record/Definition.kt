package com.magenta.rx.kotlin.record

class Definition {

    private var text: String? = null
    private var pos: String? = null
    private var ts: String? = null
    private var tr: Array<Transcription>? = null

    constructor(text: String?, pos: String?, ts: String?, tr: Array<Transcription>?) {
        this.text = text
        this.pos = pos
        this.ts = ts
        this.tr = tr
    }

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