package com.magenta.rx.kotlin.presenter

import android.text.TextUtils
import android.util.Log
import com.magenta.rx.kotlin.event.TranslateAnswerEvent
import com.magenta.rx.kotlin.loader.TranslateLoader
import com.magenta.rx.kotlin.record.TranslateAnswer
import org.greenrobot.eventbus.EventBus
import rx.Observer
import rx.schedulers.Schedulers
import javax.inject.Inject

class RetrofitPresenter @Inject constructor(private val loader: TranslateLoader) {

    fun onLoadClick(text: String) {
        loader.load(text)
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<TranslateAnswer> {
                    override fun onCompleted() {
                        Log.d(javaClass.name, "[onCompleted]")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(javaClass.name, "[onError]", e)
                    }

                    override fun onNext(translate: TranslateAnswer) {
                        Log.d(javaClass.name, "[onNext]")
                        onReceiveAnswer(text, TextUtils.join(",", translate.text))
                    }
                })
    }

    private fun join(text: Array<String>?, delimiter: String): String {
        if (text != null && text.isNotEmpty()) {
            val sb = StringBuilder(text[0])
            (1..text.size - 1).forEach { sb.append(delimiter).append(text[it]) }
            return sb.toString()
        }
        return "not found"
    }

    private fun onReceiveAnswer(text: String, translate: String) {
        EventBus.getDefault().postSticky(TranslateAnswerEvent(text, translate))
    }
}