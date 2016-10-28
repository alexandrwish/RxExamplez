package com.magenta.rx.java.module;

import com.magenta.rx.java.component.ActivityScope;
import com.magenta.rx.java.presenter.MapPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MapModule {

    @Provides
    @ActivityScope
    public MapPresenter provideMapPresenter() {
        return new MapPresenter();
    }
}