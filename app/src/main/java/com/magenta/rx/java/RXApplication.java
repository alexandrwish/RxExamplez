package com.magenta.rx.java;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.magenta.rx.java.activity.DictionaryActivity;
import com.magenta.rx.java.activity.MapActivity;
import com.magenta.rx.java.activity.RetrofitActivity;
import com.magenta.rx.java.activity.ServiceActivity;
import com.magenta.rx.java.component.DaggerRXComponent;
import com.magenta.rx.java.component.DictionaryComponent;
import com.magenta.rx.java.component.MapComponent;
import com.magenta.rx.java.component.RXComponent;
import com.magenta.rx.java.component.RetrofitComponent;
import com.magenta.rx.java.component.ServiceComponent;
import com.magenta.rx.java.db.DBAdapter;
import com.magenta.rx.java.model.entity.DaoSession;
import com.magenta.rx.java.module.DictionaryModule;
import com.magenta.rx.java.module.MapModule;
import com.magenta.rx.java.module.RXModule;
import com.magenta.rx.java.module.RetrofitModule;
import com.magenta.rx.java.module.ServiceModule;
import com.magenta.rx.java.presenter.RetrofitPresenter;
import com.magenta.rx.java.presenter.ServicePresenter;
import com.magenta.rx.kotlin.loader.ServiceLoader;
import com.magenta.rx.kotlin.loader.TranslateLoader;

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
    private ServiceComponent serviceComponent;
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

    public void inject(TranslateLoader loader) {
        retrofitComponent.inject(loader);
    }

    public void inject(RetrofitPresenter presenter) {
        retrofitComponent.inject(presenter);
    }

    public void inject(DBAdapter adapter) {
        rxComponent.inject(adapter);
    }

    public void inject(ServicePresenter presenter) {
        serviceComponent.inject(presenter);
    }

    public void inject(ServiceLoader loader) {
        serviceComponent.inject(loader);
    }
}