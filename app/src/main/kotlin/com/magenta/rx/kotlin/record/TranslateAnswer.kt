package com.magenta.rx.kotlin.record

class TranslateAnswer {

    private var code: String? = null
    private var lang: String? = null
    private var text: Array<String>? = null

    fun getCode() = code

    fun setCode(code: String) {
        this.code = code
    }

    fun getLang() = lang

    fun setLang(lang: String) {
        this.lang = lang
    }

    fun getText() = text

    fun setText(text: Array<String>) {
        this.text = text
    }
}