package com.magenta.rx.kotlin.loader

import android.content.SharedPreferences
import com.magenta.rx.java.http.TranslateClient
import javax.inject.Inject

class TranslateLoader {

    private var client: TranslateClient
    private var preferences: SharedPreferences

    @Inject
    constructor(client: TranslateClient, preferences: SharedPreferences) {
        this.client = client
        this.preferences = preferences
    }

    fun load(text: String) = client.translate(preferences.getString("translate_key", ""), text, preferences.getString("translate_lang", ""))!!
}