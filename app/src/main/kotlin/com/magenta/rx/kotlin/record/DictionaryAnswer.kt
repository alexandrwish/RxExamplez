package com.magenta.rx.kotlin.record

class DictionaryAnswer(private var head: Any?, private var def: Array<Definition>) {

    fun getHead() = head

    fun getDef() = def

    fun setHead(head: Any) {
        this.head = head
    }

    fun setDef(def: Array<Definition>) {
        this.def = def
    }
}