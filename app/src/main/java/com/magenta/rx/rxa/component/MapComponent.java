package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.activity.MapActivity;
import com.magenta.rx.rxa.module.MapModule;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {MapModule.class})
public interface MapComponent {

    void inject(MapActivity activity);
}