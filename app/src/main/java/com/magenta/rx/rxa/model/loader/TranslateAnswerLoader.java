package com.magenta.rx.rxa.model.loader;

import android.content.SharedPreferences;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.http.TranslateClient;
import com.magenta.rx.rxa.model.record.TranslateAnswer;

import javax.inject.Inject;

import rx.Observable;

public class TranslateAnswerLoader {

    @Inject
    TranslateClient client;
    @Inject
    SharedPreferences preferences;

    public TranslateAnswerLoader() {
        RXApplication.getInstance().inject(this);
    }

    public Observable<TranslateAnswer> load(String text) {
        return client.translate(preferences.getString("translate_key", ""), text, preferences.getString("translate_lang", ""));
    }
}