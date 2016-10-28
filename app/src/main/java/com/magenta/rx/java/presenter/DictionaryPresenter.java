package com.magenta.rx.java.presenter;

import android.util.Log;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.event.DictionaryAnswerEvent;
import com.magenta.rx.java.model.loader.DictionaryLoader;
import com.magenta.rx.java.model.record.Definition;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DictionaryPresenter {

    @Inject
    DictionaryLoader loader;

    public DictionaryPresenter() {
        RXApplication.getInstance().inject(this);
    }

    public void onLoadClick(final String word) {
        loader.load(word)
                .subscribeOn(Schedulers.io())
                .delay(100L, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<List<Definition>>() {
                    public void onCompleted() {
                    }

                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), e.getMessage(), e);
                    }

                    public void onNext(List<Definition> definitions) {
                        post(word, definitions);
                    }
                });
    }

    public void init() {
        loader.getPublisher().subscribe(new Action1<HashMap.Entry<String, List<Definition>>>() {
            public void call(HashMap.Entry<String, List<Definition>> stringListEntry) {
                post(stringListEntry.getKey(), stringListEntry.getValue());
            }
        });
        loader.loadAll();
    }

    private void post(String word, List<Definition> definitions) {
        EventBus.getDefault().postSticky(new DictionaryAnswerEvent(word, definitions));
    }
}