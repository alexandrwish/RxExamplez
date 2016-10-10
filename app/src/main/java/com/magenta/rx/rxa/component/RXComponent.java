package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.module.RXModule;
import com.magenta.rx.rxa.module.RetrofitModule;
import com.magenta.rx.rxa.presenter.RetrofitPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RXModule.class})
public interface RXComponent {

    RetrofitComponent plusRetrofitComponent(RetrofitModule module);

    void inject(RetrofitPresenter presenter);
}