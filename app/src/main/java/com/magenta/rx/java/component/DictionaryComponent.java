package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.DictionaryActivity;
import com.magenta.rx.java.module.DictionaryModule;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {DictionaryModule.class})
public interface DictionaryComponent {

    void inject(DictionaryActivity activity);
}