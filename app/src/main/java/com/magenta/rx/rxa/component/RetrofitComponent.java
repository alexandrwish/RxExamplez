package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.activity.RetrofitActivity;
import com.magenta.rx.rxa.model.TranslateAnswerLoader;
import com.magenta.rx.rxa.module.RetrofitModule;
import com.magenta.rx.rxa.presenter.RetrofitPresenter;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {RetrofitModule.class})
public interface RetrofitComponent {

    void inject(RetrofitActivity activity);

    void inject(TranslateAnswerLoader loader);

    void inject(RetrofitPresenter presenter);
}