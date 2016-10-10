package com.magenta.rx.rxa.presenter;

import android.util.Log;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.event.TranslateAnswerEvent;
import com.magenta.rx.rxa.model.TranslateAnswer;
import com.magenta.rx.rxa.model.TranslateAnswerLoader;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.Observer;
import rx.schedulers.Schedulers;

public class RetrofitPresenter {

    @Inject
    TranslateAnswerLoader loader;

    public RetrofitPresenter() {
        RXApplication.getInstance().inject(this);
    }

    public void onLoadClick(final String text) {
        loader.load(text)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TranslateAnswer>() {
                    public void onCompleted() {
                        Log.d(getClass().getName(), "[onCompleted]");
                    }

                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), "[onError]", e);
                    }

                    public void onNext(TranslateAnswer translate) {
                        Log.d(getClass().getName(), "[onNext]");
                        onReceiveAnswer(text, join(translate.getText(), ","));
                    }
                });
    }

    private String join(String[] text, String delimiter) {
        if (text != null && text.length > 0) {
            StringBuilder sb = new StringBuilder(text[0]);
            for (int i = 1; i < text.length; i++) {
                sb.append(delimiter).append(text[i]);
            }
            return sb.toString();
        }
        return "not found";
    }

    private void onReceiveAnswer(String text, String translate) {
        EventBus.getDefault().postSticky(new TranslateAnswerEvent(text, translate));
    }
}