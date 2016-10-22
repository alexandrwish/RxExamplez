package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.activity.ServiceMapActivity;
import com.magenta.rx.rxa.module.ServiceMapModule;
import com.magenta.rx.rxa.presenter.ServiceMapPresenter;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ServiceMapModule.class})
public interface ServiceMapComponent {

    void inject(ServiceMapActivity activity);

    void inject(ServiceMapPresenter presenter);
}