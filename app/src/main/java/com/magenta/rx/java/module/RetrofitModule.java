package com.magenta.rx.java.module;

import com.magenta.rx.java.activity.RetrofitActivity;
import com.magenta.rx.java.component.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class RetrofitModule {

    private final RetrofitActivity activity;

    public RetrofitModule(RetrofitActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public RetrofitActivity provideRetrofitActivity() {
        return activity;
    }
}