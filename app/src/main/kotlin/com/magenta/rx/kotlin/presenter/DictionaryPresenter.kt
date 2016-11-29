package com.magenta.rx.kotlin.presenter

import android.util.Log
import com.magenta.rx.kotlin.event.DictionaryAnswerEvent
import com.magenta.rx.kotlin.loader.DictionaryLoader
import com.magenta.rx.kotlin.record.Definition
import org.greenrobot.eventbus.EventBus
import rx.Subscriber
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DictionaryPresenter @Inject constructor(private val loader: DictionaryLoader) {

    init {
        this.loader.loadAll().subscribe { stringListEntry -> post(stringListEntry.first, stringListEntry.second) }
    }

    fun onLoadClick(word: String) {
        loader.load(word)
                .subscribeOn(Schedulers.io())
                .delay(100L, TimeUnit.MILLISECONDS)
                .subscribe(object : Subscriber<List<Definition>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        Log.e(javaClass.name, e.message, e)
                    }

                    override fun onNext(definitions: List<Definition>) {
                        post(word, definitions)
                    }
                })
    }

    private fun post(word: String, definitions: List<Definition>) {
        EventBus.getDefault().postSticky(DictionaryAnswerEvent(word, definitions))
    }
}