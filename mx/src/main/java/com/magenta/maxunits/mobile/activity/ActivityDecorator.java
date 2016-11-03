package com.magenta.maxunits.mobile.activity;

import android.content.Context;
import android.content.Intent;

import com.magenta.maxunits.mobile.activity.common.LoginActivity;
import com.magenta.maxunits.mobile.rpc.operations.LogoutLock;
import com.magenta.maxunits.mobile.service.CoreService;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.service.listeners.BroadcastEvent;
import com.magenta.maxunits.mobile.service.listeners.BroadcastEventsListener;
import com.magenta.maxunits.mobile.service.listeners.GenericBroadcastEventsAdapter;
import com.magenta.maxunits.mobile.service.listeners.MxBroadcastEvents;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public class ActivityDecorator {
    protected final Context context;
    private final Set<BroadcastEventsListener> registeredBroadcastEvents = new HashSet<BroadcastEventsListener>(0);

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
                        @Override
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
