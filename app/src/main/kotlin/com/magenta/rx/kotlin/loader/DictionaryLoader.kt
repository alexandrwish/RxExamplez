package com.magenta.rx.kotlin.loader

import android.content.SharedPreferences
import android.util.Log
import com.magenta.rx.java.RXApplication
import com.magenta.rx.java.http.DictionaryClient
import com.magenta.rx.java.model.converter.DictionaryConverter
import com.magenta.rx.java.model.record.Definition
import com.magenta.rx.java.model.record.DictionaryAnswer
import org.greenrobot.greendao.rx.RxDao
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.*
import javax.inject.Inject

class DictionaryLoader {

    @Inject
    lateinit var client: DictionaryClient
    @Inject
    lateinit var preferences: SharedPreferences

    private val map = HashMap<String, List<Definition>>()
    private val publishSubject = PublishSubject.create<Pair<String, List<Definition>>>()

    constructor() {
        RXApplication.getInstance().inject(this)
    }

    fun loadAll() {
        RxDao(RXApplication.getInstance().session.dictionaryEntityDao).loadAll()
                .subscribeOn(Schedulers.io())
                .flatMap { dictionaryEntities -> Observable.from(dictionaryEntities) }
                .map({ dictionaryEntity -> Pair<String, List<Definition>>(dictionaryEntity.word, Arrays.asList(*DictionaryConverter.fromEntity(dictionaryEntity).def)) })
                .doOnNext { definitions -> publishSubject.onNext(Pair(definitions.first, definitions.second)) }
                .subscribe(object : Subscriber<Pair<String, List<Definition>>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        Log.e(javaClass.name, e.message, e)
                    }

                    override fun onNext(definitions: Pair<String, List<Definition>>) {
                        map.put(definitions.first, definitions.second)
                    }
                })
    }

    fun load(word: String): Observable<List<Definition>> {
        return if (map.containsKey(word)) Observable.just<List<Definition>>(map[word]) else client.dictionary(preferences.getString("dictionary_key", ""), word, preferences.getString("dictionary_lang", ""))
                .doOnError { throwable -> Log.e(javaClass.name, throwable.message, throwable) }
                .map { dictionaryAnswer -> Arrays.asList(*dictionaryAnswer.def) }
                .doOnNext { definitions -> map.put(word, definitions) }
                .doOnNext { definitions -> DictionaryConverter.toEntity(word, DictionaryAnswer(null, definitions.toTypedArray())) }
    }

    fun getPublisher(): PublishSubject<Pair<String, List<Definition>>> {
        return publishSubject
    }
}