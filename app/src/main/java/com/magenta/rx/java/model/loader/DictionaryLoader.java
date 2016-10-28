package com.magenta.rx.java.model.loader;

import android.content.SharedPreferences;
import android.util.Log;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.http.DictionaryClient;
import com.magenta.rx.java.model.converter.DictionaryConverter;
import com.magenta.rx.java.model.entity.DictionaryEntity;
import com.magenta.rx.java.model.record.Definition;
import com.magenta.rx.java.model.record.DictionaryAnswer;

import org.greenrobot.greendao.rx.RxDao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class DictionaryLoader {

    @Inject
    DictionaryClient client;
    @Inject
    SharedPreferences preferences;

    private final Map<String, List<Definition>> map = new HashMap<>();
    private final PublishSubject<HashMap.Entry<String, List<Definition>>> publishSubject = PublishSubject.create();

    public DictionaryLoader() {
        RXApplication.getInstance().inject(this);
    }

    public void loadAll() {
        new RxDao<>(RXApplication.getInstance().getSession().getDictionaryEntityDao()).loadAll()
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<DictionaryEntity>, Observable<? extends DictionaryEntity>>() {
                    public Observable<DictionaryEntity> call(List<DictionaryEntity> dictionaryEntities) {
                        return Observable.from(dictionaryEntities);
                    }
                })
                .map(new Func1<DictionaryEntity, Map.Entry<String, List<Definition>>>() {
                    public Map.Entry<String, List<Definition>> call(DictionaryEntity dictionaryEntity) {
                        return new HashMap.SimpleEntry<>(dictionaryEntity.getWord(), Arrays.asList(DictionaryConverter.fromEntity(dictionaryEntity).getDef()));
                    }
                })
                .doOnNext(new Action1<Map.Entry<String, List<Definition>>>() {
                    public void call(Map.Entry<String, List<Definition>> definitions) {
                        publishSubject.onNext(new HashMap.SimpleEntry<>(definitions.getKey(), definitions.getValue()));
                    }
                })
                .subscribe(new Subscriber<Map.Entry<String, List<Definition>>>() {
                    public void onCompleted() {
                    }

                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), e.getMessage(), e);
                    }

                    public void onNext(Map.Entry<String, List<Definition>> definitions) {
                        map.put(definitions.getKey(), definitions.getValue());
                    }
                });
    }

    public Observable<List<Definition>> load(final String word) {
        return map.containsKey(word) ? Observable.just(map.get(word)) : client.dictionary(preferences.getString("dictionary_key", ""), word, preferences.getString("dictionary_lang", ""))
                .doOnError(new Action1<Throwable>() {
                    public void call(Throwable throwable) {
                        Log.e(getClass().getName(), throwable.getMessage(), throwable);
                    }
                })
                .map(new Func1<DictionaryAnswer, List<Definition>>() {
                    public List<Definition> call(DictionaryAnswer dictionaryAnswer) {
                        return Arrays.asList(dictionaryAnswer.getDef());
                    }
                })
                .doOnNext(new Action1<List<Definition>>() {
                    public void call(List<Definition> definitions) {
                        map.put(word, definitions);
                    }
                })
                .doOnNext(new Action1<List<Definition>>() {
                    public void call(List<Definition> definitions) {
                        DictionaryConverter.toEntity(word, new DictionaryAnswer(null, definitions.toArray(new Definition[definitions.size()])));
                    }
                });
    }

    public PublishSubject<HashMap.Entry<String, List<Definition>>> getPublisher() {
        return publishSubject;
    }
}