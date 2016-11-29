package com.magenta.rx.kotlin.loader

import android.content.SharedPreferences
import com.magenta.rx.kotlin.http.TranslateClient
import javax.inject.Inject

class TranslateLoader @Inject constructor(private val client: TranslateClient, private val preferences: SharedPreferences) {

    fun load(text: String) = client.translate(preferences.getString("translate_key", ""), text, preferences.getString("translate_lang", ""))
}