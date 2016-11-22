package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.ConcurrentActivity;
import com.magenta.rx.java.module.ConcurrentModule;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ConcurrentModule.class})
public interface ConcurrentComponent {

    void inject(ConcurrentActivity activity);
}