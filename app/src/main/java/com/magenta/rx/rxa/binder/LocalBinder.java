package com.magenta.rx.rxa.binder;

import android.app.Service;
import android.os.Binder;

public final class LocalBinder<T extends Service> extends Binder {

    private final T mService;

    public LocalBinder(T service) {
        mService = service;
    }

    public T getService() {
        return mService;
    }
}