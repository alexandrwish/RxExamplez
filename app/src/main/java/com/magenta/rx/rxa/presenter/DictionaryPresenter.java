package com.magenta.rx.rxa.presenter;

import android.util.Log;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.event.DictionaryAnswerEvent;
import com.magenta.rx.rxa.model.entity.DefinitionEntity;
import com.magenta.rx.rxa.model.loader.DictionaryLoader;

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
                .subscribe(new Subscriber<List<DefinitionEntity>>() {
                    public void onCompleted() {
                    }

                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), e.getMessage(), e);
                    }

                    public void onNext(List<DefinitionEntity> definitionEntities) {
                        post(word, definitionEntities);
                    }
                });
    }

    public void init() {
        loader.loadAll().subscribe(new Action1<HashMap.Entry<String, List<DefinitionEntity>>>() {
            public void call(HashMap.Entry<String, List<DefinitionEntity>> stringListEntry) {
                post(stringListEntry.getKey(), stringListEntry.getValue());
            }
        });
    }

    private void post(String word, List<DefinitionEntity> definitionEntities) {
        EventBus.getDefault().postSticky(new DictionaryAnswerEvent(word, definitionEntities));
    }
}