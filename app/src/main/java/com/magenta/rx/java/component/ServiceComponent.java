package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.ServiceActivity;
import com.magenta.rx.java.module.ServiceModule;
import com.magenta.rx.java.presenter.ServicePresenter;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ServiceModule.class})
public interface ServiceComponent {

    void inject(ServiceActivity activity);

    void inject(ServicePresenter presenter);
}