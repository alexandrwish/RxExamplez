package com.magenta.rx.java.module;

import com.magenta.rx.java.component.ActivityScope;
import com.magenta.rx.java.model.loader.ServiceLoader;
import com.magenta.rx.java.presenter.ServicePresenter;

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