package com.magenta.rx.kotlin.loader

import android.content.SharedPreferences
import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.http.TranslateClient
import javax.inject.Inject

class TranslateLoader {

    @Inject
    lateinit var client: TranslateClient
    @Inject
    lateinit var preferences: SharedPreferences

    constructor() {
        RXApplication.getInstance().inject(this)
    }

    fun load(text: String) = client.translate(preferences.getString("translate_key", ""), text, preferences.getString("translate_lang", ""))!!
}