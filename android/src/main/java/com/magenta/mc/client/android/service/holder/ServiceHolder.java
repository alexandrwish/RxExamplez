package com.magenta.mc.client.android.service.holder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Pair;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.listener.BindListener;
import com.magenta.mc.client.android.util.Triple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 2/28/17 use dagger inject or stay as singleton?
public final class ServiceHolder {

    private static ServiceHolder instance;
    private final Map<String, Triple<ServiceConnection, ? extends IBinder, BindListener>> binders = new ConcurrentHashMap<>();

    private ServiceHolder() {
    }

    public static ServiceHolder getInstance() {
        return instance == null ? instance = new ServiceHolder() : instance;
    }

    @SafeVarargs
    public final void bindService(Class<? extends Service> serviceClass, Pair<String, Integer>... args) {
        bindService(serviceClass, null, args);
    }

    @SafeVarargs
    public final void bindService(Class<? extends Service> serviceClass, BindListener listener, Pair<String, Integer>... args) {
        if (serviceClass == null) {
            return;
        }
        if (binders.containsKey(serviceClass.getName())) {
            if (listener != null) {
                listener.onBind(binders.get(serviceClass.getName()).second);
            }
        } else {
            final Triple<ServiceConnection, IBinder, BindListener> triple = new Triple<>();
            Bundle bundle = new Bundle();
            for (Pair<String, Integer> arg : args) {
                bundle.putInt(arg.first, arg.second);
            }
            triple.first = new ServiceConnection() {
                public void onServiceConnected(ComponentName name, IBinder service) {
                    triple.second = service;
                    if (triple.third != null) {
                        triple.third.onBind(service);
                        triple.third = null;
                    }
                }

                public void onServiceDisconnected(ComponentName name) {
                    triple.second = null;
                    triple.third = null;
                }
            };
            triple.third = listener;
            binders.put(serviceClass.getName(), triple);
            Context context = McAndroidApplication.getInstance();
            context.bindService(new Intent(context, serviceClass).putExtras(bundle), triple.first, Context.BIND_AUTO_CREATE);
        }
    }

    @SafeVarargs
    public final void startService(Class<? extends Service> serviceClass, Bundle bundle, Pair<String, Integer>... args) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (args != null) {
            for (Pair<String, Integer> arg : args) {
                bundle.putInt(arg.first, arg.second);
            }
        }
        Context context = McAndroidApplication.getInstance();
        context.startService(new Intent(context, serviceClass).putExtras(bundle));
    }

    @SafeVarargs
    public final void startService(Class<? extends Service> serviceClass, Pair<String, Integer>... args) {
        startService(serviceClass, new Bundle(), args);
    }

    public final void stopService(Class<? extends Service> serviceClass) {
        Context context = McAndroidApplication.getInstance();
        if (binders.containsKey(serviceClass.getName())) {
            context.unbindService(binders.remove(serviceClass.getName()).first);
        } else {
            context.stopService(new Intent(context, serviceClass));
        }
    }

    /**
     * @param serviceName the name of the class or interface
     *                    represented by this object.
     */
    public IBinder getService(String serviceName) {
        return binders.get(serviceName).second;
    }
}