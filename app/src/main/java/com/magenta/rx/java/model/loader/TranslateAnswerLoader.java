package com.magenta.rx.java.model.loader;

import android.content.SharedPreferences;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.http.TranslateClient;
import com.magenta.rx.java.model.record.TranslateAnswer;

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