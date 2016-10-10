package com.magenta.rx.rxa;

import android.app.Application;

import com.magenta.rx.rxa.activity.RetrofitActivity;
import com.magenta.rx.rxa.component.DaggerRXComponent;
import com.magenta.rx.rxa.component.RXComponent;
import com.magenta.rx.rxa.component.RetrofitComponent;
import com.magenta.rx.rxa.model.TranslateAnswerLoader;
import com.magenta.rx.rxa.module.RXModule;
import com.magenta.rx.rxa.module.RetrofitModule;
import com.magenta.rx.rxa.presenter.RetrofitPresenter;

public class RXApplication extends Application {

    protected static RXApplication instance;
    private RetrofitComponent retrofitComponent;
    private RXComponent rxComponent;

    public void onCreate() {
        super.onCreate();
        instance = this;
        rxComponent = DaggerRXComponent.builder().rXModule(new RXModule()).build();
    }

    public static RXApplication getInstance() {
        return instance;
    }

    public void addRetrofitComponent(RetrofitActivity activity) {
        if (retrofitComponent == null) {
            retrofitComponent = rxComponent.plusRetrofitComponent(new RetrofitModule(activity));
        }
        retrofitComponent.inject(activity);
    }

    public void removeRetrofitComponent() {
        retrofitComponent = null;
    }

    public void inject(TranslateAnswerLoader loader) {
        retrofitComponent.inject(loader);
    }

    public void inject(RetrofitPresenter presenter) {
        retrofitComponent.inject(presenter);
    }
}