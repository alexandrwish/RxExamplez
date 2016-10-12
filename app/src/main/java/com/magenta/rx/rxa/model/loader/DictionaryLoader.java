package com.magenta.rx.rxa.model.loader;

import android.content.SharedPreferences;
import android.util.Log;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.http.DictionaryClient;
import com.magenta.rx.rxa.model.entity.DefinitionEntity;
import com.magenta.rx.rxa.model.entity.DefinitionEntityDao;
import com.magenta.rx.rxa.model.entity.DictionaryEntity;
import com.magenta.rx.rxa.model.entity.DictionaryEntityDao;
import com.magenta.rx.rxa.model.entity.ExampleEntity;
import com.magenta.rx.rxa.model.entity.ExampleEntityDao;
import com.magenta.rx.rxa.model.entity.MeaningEntity;
import com.magenta.rx.rxa.model.entity.MeaningEntityDao;
import com.magenta.rx.rxa.model.entity.SynonymEntity;
import com.magenta.rx.rxa.model.entity.SynonymEntityDao;
import com.magenta.rx.rxa.model.entity.TranscriptionEntity;
import com.magenta.rx.rxa.model.entity.TranscriptionEntityDao;
import com.magenta.rx.rxa.model.record.Definition;
import com.magenta.rx.rxa.model.record.DictionaryAnswer;
import com.magenta.rx.rxa.model.record.Example;
import com.magenta.rx.rxa.model.record.Meaning;
import com.magenta.rx.rxa.model.record.Synonym;
import com.magenta.rx.rxa.model.record.Transcription;

import org.greenrobot.greendao.rx.RxDao;

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

    private final Map<String, List<DefinitionEntity>> map = new HashMap<>();
    private final PublishSubject<HashMap.Entry<String, List<DefinitionEntity>>> publishSubject = PublishSubject.create();

    public DictionaryLoader() {
        RXApplication.getInstance().inject(this);
        new RxDao<>(RXApplication.getInstance().getSession().getDictionaryEntityDao()).loadAll()
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<DictionaryEntity>, Observable<? extends DictionaryEntity>>() {
                    public Observable<DictionaryEntity> call(List<DictionaryEntity> dictionaryEntities) {
                        return Observable.from(dictionaryEntities);
                    }
                })
                .doOnNext(new Action1<DictionaryEntity>() {
                    public void call(DictionaryEntity dictionaryEntity) {
                        publishSubject.onNext(new HashMap.SimpleEntry<>(dictionaryEntity.getWord(), dictionaryEntity.getDef()));
                    }
                })
                .subscribe(new Subscriber<DictionaryEntity>() {
                    public void onCompleted() {
                    }

                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), e.getMessage(), e);
                    }

                    public void onNext(DictionaryEntity dictionaryEntity) {
                        map.put(dictionaryEntity.getWord(), dictionaryEntity.getDef());
                    }
                });
    }

    public Observable<List<DefinitionEntity>> load(final String word) {
        return map.containsKey(word) ? Observable.just(map.get(word)) : client.dictionary(preferences.getString("dictionary_key", ""), word, preferences.getString("dictionary_lang", ""))
                .doOnError(new Action1<Throwable>() {
                    public void call(Throwable throwable) {
                        Log.e(getClass().getName(), throwable.getMessage(), throwable);
                    }
                })
                .map(new Func1<DictionaryAnswer, DictionaryEntity>() {
                    public DictionaryEntity call(DictionaryAnswer dictionaryAnswer) {
                        return toEntity(word, dictionaryAnswer);
                    }
                })
                .doOnNext(new Action1<DictionaryEntity>() {
                    public void call(DictionaryEntity dictionaryEntity) {
                        map.put(dictionaryEntity.getWord(), dictionaryEntity.getDef());
                    }
                })
                .map(new Func1<DictionaryEntity, List<DefinitionEntity>>() {
                    public List<DefinitionEntity> call(DictionaryEntity dictionaryEntity) {
                        return dictionaryEntity.getDef();
                    }
                });
    }

    public PublishSubject<HashMap.Entry<String, List<DefinitionEntity>>> loadAll() {
        return publishSubject;
    }

    private DictionaryEntity toEntity(String word, DictionaryAnswer answer) {
        final DictionaryEntityDao dictionaryDao = RXApplication.getInstance().getSession().getDictionaryEntityDao();
        final DefinitionEntityDao definitionDao = RXApplication.getInstance().getSession().getDefinitionEntityDao();
        final TranscriptionEntityDao transcriptionDao = RXApplication.getInstance().getSession().getTranscriptionEntityDao();
        final ExampleEntityDao exampleDao = RXApplication.getInstance().getSession().getExampleEntityDao();
        final MeaningEntityDao meaningDao = RXApplication.getInstance().getSession().getMeaningEntityDao();
        final SynonymEntityDao synonymDao = RXApplication.getInstance().getSession().getSynonymEntityDao();
        DictionaryEntity entity = new DictionaryEntity(word);
        dictionaryDao.insert(entity);
        for (Definition definition : answer.getDef()) {
            DefinitionEntity definitionEntity = new DefinitionEntity(null, word, definition.getText(), definition.getPos());
            definitionDao.insert(definitionEntity);
            if (definition.getTr() != null) {
                for (Transcription transcription : definition.getTr()) {
                    TranscriptionEntity transcriptionEntity = new TranscriptionEntity(null, definitionEntity.getId(), transcription.getText(), transcription.getPos(), null);
                    transcriptionDao.insert(transcriptionEntity);
                    if (transcription.getEx() != null) {
                        for (Example example : transcription.getEx()) {
                            ExampleEntity exampleEntity = new ExampleEntity(null, transcriptionEntity.getId(), example.getText());
                            exampleDao.insert(exampleEntity);
                            if (example.getTr() != null) {
                                for (Transcription tr : example.getTr()) {
                                    transcriptionDao.insert(new TranscriptionEntity(null, null, tr.getText(), tr.getPos(), exampleEntity.getId()));
                                }
                            }
                        }
                        if (transcription.getMean() != null) {
                            for (Meaning meaning : transcription.getMean()) {
                                meaningDao.insert(new MeaningEntity(null, transcriptionEntity.getId(), meaning.getText()));
                            }
                        }
                        if (transcription.getSyn() != null) {
                            for (Synonym synonym : transcription.getSyn()) {
                                synonymDao.insert(new SynonymEntity(null, transcriptionEntity.getId(), synonym.getText()));
                            }
                        }
                    }
                }
            }
        }
        return entity;
    }
}