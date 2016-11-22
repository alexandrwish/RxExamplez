package com.magenta.rx.java.module;

import com.magenta.rx.java.activity.ConcurrentActivity;
import com.magenta.rx.java.component.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ConcurrentModule {

    private final ConcurrentActivity activity;

    public ConcurrentModule(ConcurrentActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public ConcurrentActivity provideConcurrentActivity() {
        return activity;
    }
}