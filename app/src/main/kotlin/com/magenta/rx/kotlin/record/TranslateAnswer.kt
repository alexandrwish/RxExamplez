package com.magenta.rx.kotlin.record

class TranslateAnswer(private var code: String?, private var lang: String?, private var text: Array<String>?) {

    fun getCode() = code

    fun getLang() = lang

    fun getText() = text

    fun setCode(code: String) {
        this.code = code
    }

    fun setLang(lang: String) {
        this.lang = lang
    }

    fun setText(text: Array<String>) {
        this.text = text
    }
}