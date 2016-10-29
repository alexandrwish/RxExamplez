package com.magenta.rx.kotlin.record

class DictionaryAnswer {

    private var head: Any? = null
    private lateinit var def: Array<Definition>

    constructor(head: Any?, def: Array<Definition>) {
        this.head = head
        this.def = def
    }

    constructor()

    fun getHead() = head

    fun setHead(head: Any) {
        this.head = head
    }

    fun getDef() = def

    fun setDef(def: Array<Definition>) {
        this.def = def
    }
}