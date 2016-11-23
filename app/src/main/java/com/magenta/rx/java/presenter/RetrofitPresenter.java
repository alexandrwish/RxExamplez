package com.magenta.rx.java.presenter;

import android.util.Log;

import com.magenta.rx.kotlin.event.TranslateAnswerEvent;
import com.magenta.rx.kotlin.loader.TranslateLoader;
import com.magenta.rx.kotlin.record.TranslateAnswer;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.Observer;
import rx.schedulers.Schedulers;

public class RetrofitPresenter {

    private final TranslateLoader loader;

    @Inject
    public RetrofitPresenter(TranslateLoader loader) {
        this.loader = loader;
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