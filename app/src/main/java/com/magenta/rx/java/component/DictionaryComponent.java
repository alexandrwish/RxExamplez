package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.DictionaryActivity;
import com.magenta.rx.java.module.DictionaryModule;
import com.magenta.rx.java.presenter.DictionaryPresenter;
import com.magenta.rx.kotlin.loader.DictionaryLoader;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {DictionaryModule.class})
public interface DictionaryComponent {

    void inject(DictionaryActivity activity);

    void inject(DictionaryLoader loader);

    void inject(DictionaryPresenter presenter);
}