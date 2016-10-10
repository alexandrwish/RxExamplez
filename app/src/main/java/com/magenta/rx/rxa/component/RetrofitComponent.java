package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.activity.RetrofitActivity;
import com.magenta.rx.rxa.module.RetrofitModule;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {RetrofitModule.class})
public interface RetrofitComponent {

    void inject(RetrofitActivity activity);
}