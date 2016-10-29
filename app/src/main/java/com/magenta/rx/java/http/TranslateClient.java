package com.magenta.rx.java.http;

import com.magenta.rx.kotlin.record.TranslateAnswer;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TranslateClient {

    @GET(value = "/api/v1.5/tr.json/translate")
    Observable<TranslateAnswer> translate(@Query("key") String key, @Query("text") String text, @Query("lang") String lang);
}