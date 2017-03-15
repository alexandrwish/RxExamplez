package com.magenta.mc.client.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.magenta.mc.client.android.binder.SocketBinder;
import com.magenta.mc.client.android.service.CoreService;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.service.listeners.BroadcastEvent;
import com.magenta.mc.client.android.service.listeners.BroadcastEventsListener;
import com.magenta.mc.client.android.service.listeners.GenericBroadcastEventsAdapter;
import com.magenta.mc.client.android.service.listeners.MxBroadcastEvents;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ActivityDecorator {

    protected final Context context;

    private final Set<BroadcastEventsListener> registeredBroadcastEvents = new HashSet<>(0);

    public ActivityDecorator(final Context context) {
        this.context = context;
    }

    public void onCreate() {
        registerListener();
    }

    public void onResume() {
        registerListener();
        IBinder binder = ServiceHolder.getInstance().getService(SocketIOService.class.getName());
        if (binder != null) {
            ((SocketBinder)binder).subscribe(this);
        }
    }

    public void onPause() {
        removeListener();
        IBinder binder = ServiceHolder.getInstance().getService(SocketIOService.class.getName());
        if (binder != null) {
            ((SocketBinder)binder).unsubscribe();
        }
    }

    private synchronized void removeListener() {
        final CoreService coreService = ServicesRegistry.getCoreService();
        if (coreService != null) {
            for (final BroadcastEventsListener listener : registeredBroadcastEvents) {
                coreService.removeListener(listener);
            }
        }
        registeredBroadcastEvents.clear();
    }

    @SuppressWarnings({"unchecked"})
    private synchronized void registerListener() {
        removeListener();
        final CoreService coreService = ServicesRegistry.getCoreService();
        if (coreService != null) {
            final Method[] methods = context.getClass().getMethods();
            for (final Method method : methods) {
                final MxBroadcastEvents mxBroadcastEvents = method.getAnnotation(MxBroadcastEvents.class);
                if (mxBroadcastEvents != null) {
                    final String id = context.getClass().getName() + "#" + method.getName();
                    final BroadcastEventsListener listener = new GenericBroadcastEventsAdapter(id, mxBroadcastEvents.value()) {

                        public void onEvent(final BroadcastEvent event) {
                            try {
                                method.invoke(context, event);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                    registeredBroadcastEvents.add(listener);
                    coreService.registerListener(listener);
                }
            }
        }
    }

    public void doLogout() {
        context.startActivity(new Intent(context, LoginActivity.class));
        // TODO: 3/12/17 logout
    }
}