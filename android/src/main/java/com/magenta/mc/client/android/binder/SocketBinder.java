package com.magenta.mc.client.android.binder;

import android.os.Binder;
import android.util.Pair;

import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.ui.activity.ActivityDecorator;

import rx.Subscriber;
import rx.Subscription;

public class SocketBinder extends Binder {

    private final SocketIOService service;
    private Subscription subscription;

    public SocketBinder(SocketIOService service) {
        this.service = service;
    }

    public void subscribe(ActivityDecorator activityDecorator) {
        subscription = service.getPublisher().subscribe(new Subscriber<Pair<Long, String>>() {
            public void onCompleted() {

            }

            public void onError(Throwable e) {

            }

            public void onNext(Pair<Long, String> pair) {

            }
        });
    }

    public void unsubscribe() {
        subscription.unsubscribe();
    }
}