package com.magenta.rx.kotlin.loader

import android.content.SharedPreferences
import android.util.Log
import com.magenta.rx.java.RXApplication
import com.magenta.rx.kotlin.http.DictionaryClient
import com.magenta.rx.kotlin.record.Definition
import com.magenta.rx.kotlin.record.DictionaryAnswer
import com.magenta.rx.kotlin.utils.convert
import com.magenta.rx.kotlin.utils.toEntity
import org.greenrobot.greendao.rx.RxDao
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class DictionaryLoader @Inject constructor(private val client: DictionaryClient, private val preferences: SharedPreferences) {

    private val map = HashMap<String, List<Definition>>()

    fun loadAll() = RxDao(RXApplication.getInstance().session.dictionaryEntityDao).loadAll()
            .subscribeOn(Schedulers.io())
            .flatMap { dictionaryEntities -> Observable.from(dictionaryEntities) }
            .map { dictionaryEntity -> Pair<String, List<Definition>>(dictionaryEntity.word, Arrays.asList(*convert(dictionaryEntity).def)) }
            .doOnNext { definitions -> map.put(definitions.first, definitions.second) }!!

    fun load(word: String) = (if (map.containsKey(word)) Observable.just<List<Definition>>(map[word]) else client.dictionary(preferences.getString("dictionary_key", ""), word, preferences.getString("dictionary_lang", ""))
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.e(javaClass.name, throwable.message, throwable) }
            .map { dictionaryAnswer -> Arrays.asList(*dictionaryAnswer.def) }
            .doOnNext { definitions -> map.put(word, definitions) }
            .doOnNext { definitions -> toEntity(word, DictionaryAnswer(null, definitions.toTypedArray())) })!!
}