package com.magenta.rx.java;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.magenta.rx.java.component.DaggerRXComponent;
import com.magenta.rx.java.db.DBAdapter;
import com.magenta.rx.java.model.entity.DaoSession;
import com.magenta.rx.java.module.RXModule;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

public class RXApplication extends Application {

    protected static RXApplication instance;
    private DaggerHolder holder;

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
        holder = new DaggerHolder(DaggerRXComponent.builder().rXModule(new RXModule()).build());
    }

    public DaggerHolder getHolder() {
        return holder;
    }

    public static RXApplication getInstance() {
        return instance;
    }

    public DaoSession getSession() {
        return adapter.getMainSession();
    }
}