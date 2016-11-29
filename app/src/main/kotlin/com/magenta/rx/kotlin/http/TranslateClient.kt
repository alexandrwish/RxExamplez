package com.magenta.rx.kotlin.http

import com.magenta.rx.kotlin.record.TranslateAnswer
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface TranslateClient {

    @GET("/api/v1.5/tr.json/translate") fun translate(@Query("key") key: String, @Query("text") text: String, @Query("lang") lang: String): Observable<TranslateAnswer>
}