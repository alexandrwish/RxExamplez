package com.magenta.rx.rxa.module;

import com.magenta.rx.rxa.component.ActivityScope;
import com.magenta.rx.rxa.presenter.MapPresenter;

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