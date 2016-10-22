package com.magenta.rx.rxa.module;

import com.magenta.rx.rxa.component.ActivityScope;
import com.magenta.rx.rxa.model.loader.GeoLocationLoader;
import com.magenta.rx.rxa.presenter.ServiceMapPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceMapModule {

    @Provides
    @ActivityScope
    public ServiceMapPresenter provideServiceMapPresenter() {
        return new ServiceMapPresenter();
    }

    @Provides
    @ActivityScope
    public GeoLocationLoader provideGeoLocationLoader() {
        return new GeoLocationLoader();
    }
}