package com.magenta.mc.client.android.service.holder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Pair;

import com.magenta.mc.client.android.service.listeners.BindListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 2/28/17 use dagger inject or stay as singleton?
public class ServiceHolder {

    private static ServiceHolder instance;
    private final Map<String, IBinder> binders = new ConcurrentHashMap<>();
    private final Map<String, BindListener> bindCallbacks = new ConcurrentHashMap<>();

    private final ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            binders.put(name.getClassName(), service);
            if (bindCallbacks.containsKey(name.getClassName())) {
                bindCallbacks.remove(name.getClassName()).onBind(service);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            binders.remove(name.getClassName());
            bindCallbacks.remove(name.getClassName());
        }
    };

    private ServiceHolder() {
    }

    public static ServiceHolder getInstance() {
        return instance == null ? instance = new ServiceHolder() : instance;
    }

    @SafeVarargs
    public final void bindService(Context context, Class<? extends Service> serviceClass, Pair<String, Integer>... args) {
        bindService(context, serviceClass, null, args);
    }

    @SafeVarargs
    public final void bindService(Context context, Class<? extends Service> serviceClass, BindListener listener, Pair<String, Integer>... args) {
        if (serviceClass == null || context == null) {
            return;
        }
        if (listener != null) {
            bindCallbacks.put(serviceClass.getName(), listener);
        }
        Bundle bundle = new Bundle();
        if (args != null) {
            for (Pair<String, Integer> arg : args) {
                bundle.putInt(arg.first, arg.second);
            }
        }
        context.bindService(new Intent(context, serviceClass).putExtras(bundle), connection, Context.BIND_AUTO_CREATE);
    }

    @SafeVarargs
    public final void startService(Context context, Class<? extends Service> serviceClass, Pair<String, Integer>... args) {
        Bundle bundle = new Bundle();
        if (args != null) {
            for (Pair<String, Integer> arg : args) {
                bundle.putInt(arg.first, arg.second);
            }
        }
        context.startService(new Intent(context, serviceClass).putExtras(bundle));
    }

    /**
     * @param serviceName the name of the class or interface
     *                    represented by this object.
     */
    public IBinder getService(String serviceName) {
        return binders.get(serviceName);
    }
}