package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.RetrofitActivity;
import com.magenta.rx.java.module.RetrofitModule;
import com.magenta.rx.java.presenter.RetrofitPresenter;
import com.magenta.rx.kotlin.loader.TranslateLoader;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {RetrofitModule.class})
public interface RetrofitComponent {

    void inject(RetrofitActivity activity);

    void inject(TranslateLoader loader);

    void inject(RetrofitPresenter presenter);

    RetrofitPresenter retrofitPresenter();
}