package com.magenta.rx.java.presenter;

import android.location.Location;

import com.magenta.rx.java.RXApplication;
import com.magenta.rx.java.event.ReceivedLocationEvent;
import com.magenta.rx.kotlin.loader.ServiceLoader;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ServicePresenter {

    @Inject
    ServiceLoader loader;

    @Inject
    public ServicePresenter() {
        RXApplication.getInstance().inject(this);
    }

    public void load(Observable<Location> observable) {
        observable.observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Action1<Location>() {
            public void call(Location location) {
                EventBus.getDefault().postSticky(new ReceivedLocationEvent(location.getLatitude(), location.getLongitude(), location.getTime()));
            }
        });
    }
}