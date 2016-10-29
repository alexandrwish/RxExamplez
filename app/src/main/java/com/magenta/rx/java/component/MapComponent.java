package com.magenta.rx.java.component;

import com.magenta.rx.java.activity.MapActivity;
import com.magenta.rx.java.module.MapModule;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {MapModule.class})
public interface MapComponent {

    void inject(MapActivity activity);
}