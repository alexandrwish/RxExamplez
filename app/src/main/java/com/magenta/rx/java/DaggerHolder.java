package com.magenta.rx.java;

import com.magenta.rx.java.activity.DictionaryActivity;
import com.magenta.rx.java.activity.MapActivity;
import com.magenta.rx.java.activity.RetrofitActivity;
import com.magenta.rx.java.activity.ServiceActivity;
import com.magenta.rx.java.component.DictionaryComponent;
import com.magenta.rx.java.component.MapComponent;
import com.magenta.rx.java.component.RXComponent;
import com.magenta.rx.java.component.RetrofitComponent;
import com.magenta.rx.java.component.ServiceComponent;
import com.magenta.rx.java.module.DictionaryModule;
import com.magenta.rx.java.module.MapModule;
import com.magenta.rx.java.module.RetrofitModule;
import com.magenta.rx.java.module.ServiceModule;

public class DaggerHolder {

    private final RXComponent rxComponent;
    private MapComponent mapComponent;
    private ServiceComponent serviceComponent;
    private RetrofitComponent retrofitComponent;
    private DictionaryComponent dictionaryComponent;

    public DaggerHolder(RXComponent rxComponent) {
        this.rxComponent = rxComponent;
        rxComponent.inject(RXApplication.getInstance());
    }

    public void addRetrofitComponent(RetrofitActivity activity) {
        if (retrofitComponent == null) {
            retrofitComponent = rxComponent.plusRetrofitComponent(new RetrofitModule(activity));
        }
        retrofitComponent.inject(activity);
    }

    public void addMapComponent(MapActivity activity) {
        if (mapComponent == null) {
            mapComponent = rxComponent.plusMapComponent(new MapModule());
        }
        mapComponent.inject(activity);
    }

    public void addDictionaryComponent(DictionaryActivity activity) {
        if (dictionaryComponent == null) {
            dictionaryComponent = rxComponent.plusDictionaryComponent(new DictionaryModule(activity));
        }
        dictionaryComponent.inject(activity);
    }

    public void addServiceComponent(ServiceActivity activity) {
        if (serviceComponent == null) {
            serviceComponent = rxComponent.plusServiceComponent(new ServiceModule());
        }
        serviceComponent.inject(activity);
    }

    public void removeRetrofitComponent() {
        retrofitComponent = null;
    }

    public void removeMapComponent() {
        mapComponent = null;
    }

    public void removeDictionaryComponent() {
        dictionaryComponent = null;
    }

    public void removeServiceComponent() {
        serviceComponent = null;
    }
}
