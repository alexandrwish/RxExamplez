package com.magenta.rx.java.http;

import com.magenta.rx.java.model.record.DictionaryAnswer;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface DictionaryClient {

    @GET(value = "/api/v1/dicservice.json/lookup")
    Observable<DictionaryAnswer> dictionary(@Query("key") String key, @Query("text") String text, @Query("lang") String lang);
}