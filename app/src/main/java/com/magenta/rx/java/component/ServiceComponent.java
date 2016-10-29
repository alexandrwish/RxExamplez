package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.ServiceActivity;
import com.magenta.rx.java.module.ServiceModule;
import com.magenta.rx.java.presenter.ServicePresenter;
import com.magenta.rx.kotlin.loader.ServiceLoader;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ServiceModule.class})
public interface ServiceComponent {

    void inject(ServicePresenter presenter);

    void inject(ServiceActivity activity);

    void inject(ServiceLoader loader);

    ServicePresenter servicePresenter();
}