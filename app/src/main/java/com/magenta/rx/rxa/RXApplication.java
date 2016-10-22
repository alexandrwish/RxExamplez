package com.magenta.rx.rxa;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.magenta.rx.rxa.activity.DictionaryActivity;
import com.magenta.rx.rxa.activity.MapActivity;
import com.magenta.rx.rxa.activity.RetrofitActivity;
import com.magenta.rx.rxa.activity.ServiceMapActivity;
import com.magenta.rx.rxa.component.DaggerRXComponent;
import com.magenta.rx.rxa.component.DictionaryComponent;
import com.magenta.rx.rxa.component.MapComponent;
import com.magenta.rx.rxa.component.RXComponent;
import com.magenta.rx.rxa.component.RetrofitComponent;
import com.magenta.rx.rxa.component.ServiceMapComponent;
import com.magenta.rx.rxa.db.DBAdapter;
import com.magenta.rx.rxa.model.entity.DaoSession;
import com.magenta.rx.rxa.model.loader.DictionaryLoader;
import com.magenta.rx.rxa.model.loader.TranslateAnswerLoader;
import com.magenta.rx.rxa.module.DictionaryModule;
import com.magenta.rx.rxa.module.MapModule;
import com.magenta.rx.rxa.module.RXModule;
import com.magenta.rx.rxa.module.RetrofitModule;
import com.magenta.rx.rxa.module.ServiceMapModule;
import com.magenta.rx.rxa.presenter.DictionaryPresenter;
import com.magenta.rx.rxa.presenter.RetrofitPresenter;
import com.magenta.rx.rxa.presenter.ServiceMapPresenter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

public class RXApplication extends Application {

    protected static RXApplication instance;
    private RetrofitComponent retrofitComponent;
    private RXComponent rxComponent;
    private MapComponent mapComponent;
    private ServiceMapComponent serviceMapComponent;
    private DictionaryComponent dictionaryComponent;

    @Inject
    DBAdapter adapter;

    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(getResources().getAssets().open("application.properties")));
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                editor.putString(entry.getKey().toString(), entry.getValue().toString());
            }
            editor.apply();
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
        rxComponent = DaggerRXComponent.builder().rXModule(new RXModule()).build();
        rxComponent.inject(this);
    }

    public static RXApplication getInstance() {
        return instance;
    }

    public DaoSession getSession() {
        return adapter.getMainSession();
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

    public void addServiceMapComponent(ServiceMapActivity activity) {
        if (serviceMapComponent == null) {
            serviceMapComponent = rxComponent.plusServiceMapComponent(new ServiceMapModule());
        }
        serviceMapComponent.inject(activity);
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

    public void removeServiceMapComponent() {
        serviceMapComponent = null;
    }

    public void inject(TranslateAnswerLoader loader) {
        retrofitComponent.inject(loader);
    }

    public void inject(RetrofitPresenter presenter) {
        retrofitComponent.inject(presenter);
    }

    public void inject(DictionaryLoader loader) {
        dictionaryComponent.inject(loader);
    }

    public void inject(DictionaryPresenter presenter) {
        dictionaryComponent.inject(presenter);
    }

    public void inject(DBAdapter adapter) {
        rxComponent.inject(adapter);
    }

    public void inject(ServiceMapPresenter presenter) {
        serviceMapComponent.inject(presenter);
    }
}