package com.magenta.mc.client.android.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.binder.SocketBinder;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.events.AlertEvent;
import com.magenta.mc.client.android.service.CoreService;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.service.listeners.BroadcastEvent;
import com.magenta.mc.client.android.service.listeners.BroadcastEventsListener;
import com.magenta.mc.client.android.service.listeners.GenericBroadcastEventsAdapter;
import com.magenta.mc.client.android.service.listeners.MxBroadcastEvents;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityDecorator {

    protected final Context context;

    private final Set<BroadcastEventsListener> registeredBroadcastEvents = new HashSet<>(0);
    private AlertDialog alertDialog;

    public ActivityDecorator(final Context context) {
        this.context = context;
    }

    public void onResume() {
        registerListener();
    }

    public void onPause() {
        removeListener();
        IBinder binder = ServiceHolder.getInstance().getService(SocketIOService.class.getName());
        if (binder != null) {
            ((SocketBinder) binder).unsubscribe();
        }
    }

    public void showAlert(final AlertEvent event) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(context)
                    .setMessage(R.string.performer_alert)
                    .setPositiveButton(R.string.mx_show, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<String> jobs = event.getJobs();
                            List<String> runs = event.getRuns();
                            Intent intent;
                            if (runs.size() == 1) {
                                if (jobs.size() == 1) {
                                    intent = new Intent(context, StartActivity.class).putExtra(IntentAttributes.STOP_ID, jobs.get(0));
                                } else {
                                    intent = new Intent(context, JobActivity.class);
                                }
                                intent.putExtra(IntentAttributes.JOB_ID, runs.get(0));
                            } else {
                                intent = new Intent(context, JobsActivity.class);
                            }
                            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    })
                    .setNegativeButton(R.string.mx_ignore, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //ignore
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialogInterface) {
                            //ignore
                        }
                    })
                    .show();
        } else if (!alertDialog.isShowing()) {
            alertDialog.show();
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
}