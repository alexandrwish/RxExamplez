package com.magenta.maxunits.mobile.dlib.activity;

import android.content.Context;
import android.content.Intent;

import com.magenta.maxunits.mobile.dlib.activity.common.LoginActivity;
import com.magenta.maxunits.mobile.dlib.rpc.operations.LogoutLock;
import com.magenta.maxunits.mobile.dlib.service.CoreService;
import com.magenta.maxunits.mobile.dlib.service.ServicesRegistry;
import com.magenta.maxunits.mobile.dlib.service.listeners.BroadcastEvent;
import com.magenta.maxunits.mobile.dlib.service.listeners.BroadcastEventsListener;
import com.magenta.maxunits.mobile.dlib.service.listeners.GenericBroadcastEventsAdapter;
import com.magenta.maxunits.mobile.dlib.service.listeners.MxBroadcastEvents;

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

    public void onPause() {
        removeListener();
    }

    public void onResume() {
        registerListener();
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
        LogoutLock.getInstance().logout();
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}