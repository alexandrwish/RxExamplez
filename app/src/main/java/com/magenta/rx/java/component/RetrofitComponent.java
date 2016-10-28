package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.RetrofitActivity;
import com.magenta.rx.java.model.loader.TranslateAnswerLoader;
import com.magenta.rx.java.module.RetrofitModule;
import com.magenta.rx.java.presenter.RetrofitPresenter;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {RetrofitModule.class})
public interface RetrofitComponent {

    void inject(RetrofitActivity activity);

    void inject(TranslateAnswerLoader loader);

    void inject(RetrofitPresenter presenter);
}