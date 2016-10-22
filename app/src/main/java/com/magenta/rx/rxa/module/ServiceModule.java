package com.magenta.rx.rxa.module;

import com.magenta.rx.rxa.component.ActivityScope;
import com.magenta.rx.rxa.model.loader.ServiceLoader;
import com.magenta.rx.rxa.presenter.ServicePresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    @ActivityScope
    public ServicePresenter provideServicePresenter() {
        return new ServicePresenter();
    }

    @Provides
    @ActivityScope
    public ServiceLoader provideServiceLoader() {
        return new ServiceLoader();
    }
}