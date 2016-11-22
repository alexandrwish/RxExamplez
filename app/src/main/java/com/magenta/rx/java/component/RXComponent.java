package com.magenta.rx.java.component;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.module.ConcurrentModule;
import com.magenta.rx.java.module.DictionaryModule;
import com.magenta.rx.java.module.MapModule;
import com.magenta.rx.java.module.RXModule;
import com.magenta.rx.java.module.RetrofitModule;
import com.magenta.rx.java.module.ServiceModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RXModule.class})
public interface RXComponent {

    DictionaryComponent plusDictionaryComponent(DictionaryModule module);

    ConcurrentComponent plusConcurrentComponent(ConcurrentModule module);

    RetrofitComponent plusRetrofitComponent(RetrofitModule module);

    ServiceComponent plusServiceComponent(ServiceModule module);

    MapComponent plusMapComponent(MapModule module);

    void inject(RXApplication application);
}