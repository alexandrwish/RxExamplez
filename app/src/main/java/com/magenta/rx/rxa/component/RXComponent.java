package com.magenta.rx.rxa.component;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.db.DBAdapter;
import com.magenta.rx.rxa.module.DictionaryModule;
import com.magenta.rx.rxa.module.MapModule;
import com.magenta.rx.rxa.module.RXModule;
import com.magenta.rx.rxa.module.RetrofitModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RXModule.class})
public interface RXComponent {

    RetrofitComponent plusRetrofitComponent(RetrofitModule module);

    MapComponent plusMapComponent(MapModule module);

    DictionaryComponent plusDictionaryComponent(DictionaryModule module);

    void inject(RXApplication application);

    void inject(DBAdapter adapter);
}