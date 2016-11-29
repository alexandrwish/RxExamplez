package com.magenta.rx.kotlin.http

import com.magenta.rx.kotlin.record.DictionaryAnswer
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface DictionaryClient {

    @GET("/api/v1/dicservice.json/lookup") fun dictionary(@Query("key") key: String, @Query("text") text: String, @Query("lang") lang: String): Observable<DictionaryAnswer>
}