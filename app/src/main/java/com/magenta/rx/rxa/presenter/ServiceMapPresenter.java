package com.magenta.rx.rxa.presenter;

import android.location.Location;

import com.magenta.rx.rxa.RXApplication;
import com.magenta.rx.rxa.event.ReceivedLocationEvent;
import com.magenta.rx.rxa.model.loader.GeoLocationLoader;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ServiceMapPresenter {

    @Inject
    GeoLocationLoader loader;

    public ServiceMapPresenter() {
        RXApplication.getInstance().inject(this);
    }

    public void load() {
        loader.load().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Action1<Location>() {
            public void call(Location location) {
                EventBus.getDefault().postSticky(new ReceivedLocationEvent(location.getLatitude(), location.getLongitude(), location.getTime()));
            }
        });
    }
}