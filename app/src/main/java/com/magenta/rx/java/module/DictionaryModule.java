package com.magenta.rx.java.module;

import com.magenta.rx.java.activity.DictionaryActivity;
import com.magenta.rx.java.component.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class DictionaryModule {

    private final DictionaryActivity activity;

    public DictionaryModule(DictionaryActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public DictionaryActivity provideDictionaryActivity() {
        return activity;
    }
}