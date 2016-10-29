package com.magenta.rx.java.module;

import com.magenta.rx.java.component.ActivityScope;
import com.magenta.rx.kotlin.loader.ServiceLoader;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    @ActivityScope
    public ServiceLoader provideServiceLoader() {
        return new ServiceLoader();
    }
}