package com.magenta.mc.client.android.binder;

import android.os.Binder;
import android.util.Pair;

import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.HttpService;
import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class SocketBinder extends Binder {

    private final SocketIOService service;
    private Subscription subscription;
    private Long timestamp;

    public SocketBinder(SocketIOService service) {
        this.service = service;
    }

    public void subscribe() {
        subscription = service.getPublisher()
                .doOnNext(new Action1<Pair<Long, String>>() {
                    public void call(Pair<Long, String> pair) {
                        timestamp = pair.first;
                    }
                })
                .delay(5, TimeUnit.SECONDS)
                .filter(new Func1<Pair<Long, String>, Boolean>() {
                    public Boolean call(Pair<Long, String> pair) {
                        return timestamp - 5000 - pair.first <= 0;
                    }
                })
                .subscribe(new Subscriber<Pair<Long, String>>() {
                    public void onCompleted() {

                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(SocketBinder.class).error(e.getMessage(), e);
                    }

                    public void onNext(Pair<Long, String> pair) {
                        ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.JOBS_TYPE));
                    }
                });
    }

    // TODO: 04/04/2017 unsubscribr on logout
    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}