package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.activity.ServiceActivity;
import com.magenta.rx.rxa.module.ServiceModule;
import com.magenta.rx.rxa.presenter.ServicePresenter;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ServiceModule.class})
public interface ServiceComponent {

    void inject(ServiceActivity activity);

    void inject(ServicePresenter presenter);
}