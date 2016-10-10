package com.magenta.rx.rxa.model;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.http.APIClient;

import javax.inject.Inject;

import rx.Observable;

public class TranslateAnswerLoader {

    @Inject
    APIClient client;

    public TranslateAnswerLoader() {
        RXApplication.getInstance().inject(this);
    }

    public Observable<TranslateAnswer> load(String text) {
        return client.translate("trnsl.1.1.20161008T152042Z.47ba7d102a5a0487.48abde3433e4c3dbb46961c913f32db6e6ec1c1c", text, "ru");
    }
}