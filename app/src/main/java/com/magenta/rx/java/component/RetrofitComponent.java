package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.RetrofitActivity;
import com.magenta.rx.java.module.RetrofitModule;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {RetrofitModule.class})
public interface RetrofitComponent {

    void inject(RetrofitActivity activity);
}