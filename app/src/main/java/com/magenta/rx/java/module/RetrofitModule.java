package com.magenta.rx.java.module;

import android.app.Activity;

import com.magenta.rx.java.activity.RetrofitActivity;
import com.magenta.rx.java.component.ActivityScope;
import com.magenta.rx.java.view.RetrofitViewHolder;
import com.magenta.rx.kotlin.loader.TranslateLoader;

import dagger.Module;
import dagger.Provides;

@Module
public class RetrofitModule {

    private RetrofitActivity activity;

    public RetrofitModule(RetrofitActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideRetrofitActivity() {
        return activity;
    }

    @Provides
    @ActivityScope
    public RetrofitViewHolder provideRetrofitHolder() {
        return new RetrofitViewHolder(activity);
    }

    @Provides
    @ActivityScope
    public TranslateLoader provideTranslateLoader() {
        return new TranslateLoader();
    }
}