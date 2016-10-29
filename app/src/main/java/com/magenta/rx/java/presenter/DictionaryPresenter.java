package com.magenta.rx.java.presenter;

import android.util.Log;

import com.magenta.rx.java.event.DictionaryAnswerEvent;
import com.magenta.rx.java.model.record.Definition;
import com.magenta.rx.kotlin.loader.DictionaryLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import kotlin.Pair;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DictionaryPresenter {

    private DictionaryLoader loader;

    @Inject
    public DictionaryPresenter(DictionaryLoader loader) {
        this.loader = loader;
        init();
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

    private void init() {
        loader.getPublisher().subscribe(new Action1<Pair<String, List<Definition>>>() {
            public void call(Pair<String, List<Definition>> stringListEntry) {
                post(stringListEntry.getFirst(), stringListEntry.getSecond());
            }
        });
        loader.loadAll();
    }

    private void post(String word, List<Definition> definitions) {
        EventBus.getDefault().postSticky(new DictionaryAnswerEvent(word, definitions));
    }
}