package com.magenta.rx.rxa.module;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;
import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.db.DBAdapter;
import com.magenta.rx.rxa.http.DictionaryClient;
import com.magenta.rx.rxa.http.TranslateClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RXModule {

    @Provides
    @Singleton
    public TranslateClient provideTranslateClient() {
        return new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(TranslateClient.class);
    }

    @Provides
    @Singleton
    public DictionaryClient provideDictionaryClient() {
        return new Retrofit.Builder()
                .baseUrl("https://dictionary.yandex.net")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(DictionaryClient.class);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(RXApplication.getInstance());
    }

    @Provides
    @Singleton
    public DBAdapter provideDBHelper() {
        return new DBAdapter(RXApplication.getInstance());
    }
}