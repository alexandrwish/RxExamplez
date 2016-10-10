package com.magenta.rx.rxa.http;

import com.magenta.rx.rxa.model.TranslateAnswer;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface APIClient {

    @GET(value = "/api/v1.5/tr.json/translate")
    Observable<TranslateAnswer> translate(@Query("key") String key, @Query("text") String text, @Query("lang") String lang);
}