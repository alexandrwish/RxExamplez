package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.activity.DictionaryActivity;
import com.magenta.rx.rxa.model.loader.DictionaryLoader;
import com.magenta.rx.rxa.module.DictionaryModule;
import com.magenta.rx.rxa.presenter.DictionaryPresenter;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {DictionaryModule.class})
public interface DictionaryComponent {

    void inject(DictionaryActivity activity);

    void inject(DictionaryLoader loader);

    void inject(DictionaryPresenter presenter);
}