package com.magenta.maxunits.mobile.dlib.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.activity.WorkflowActivity;
import com.magenta.maxunits.mobile.dlib.activity.JobsActivity;
import com.magenta.maxunits.mobile.dlib.service.events.EventType;
import com.magenta.maxunits.mobile.dlib.service.events.JobEvent;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.service.CoreServiceGeneric;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.service.listeners.BroadcastEvent;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.Notifications;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey Grachev
 */
public class CoreServiceImpl extends CoreServiceGeneric {

    ReentrantLock alertLock = new ReentrantLock(true);
    AlertDialog alertDialog;
    Context context;
    MediaPlayer mediaPlayer;

    @SuppressWarnings("unchecked")
    public void notifyListeners(BroadcastEvent event) {
        super.notifyListeners(event);
        if (event.isAny(EventType.NEW_JOB, EventType.JOB_UPDATED, EventType.JOB_CANCELLED)) {
            Activity activity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
            if (((JobEvent) event).isRequireAlert()) {
                processEvent((JobEvent) event, activity);
            } else if (event.is(EventType.JOB_CANCELLED)) {
                context.startActivity(new Intent(context, ServicesRegistry.getWorkflowService().getFirstActivity()));
            }
        }
    }

    private void processEvent(JobEvent event, final Activity activity) {
        final boolean closeWorkflowActivity = event.is(EventType.JOB_CANCELLED);
        final String title = activity.getString(R.string.msg_schedule_updated_title);
        final String message = activity.getString(R.string.msg_schedule_updated_message);
        if (!activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    showDialog(activity, title, message, closeWorkflowActivity);
                }
            });
        } else {
            showNotification(title, message, closeWorkflowActivity);
        }
        if (MxSettings.getInstance().isIncomingUpdatePlaySoundEnabled()) {
            try {
                mediaPlayer.start();
            } catch (Exception ignore) {
                MCLoggerFactory.getLogger(getClass()).error("Stub!!", ignore);
            }
        }
    }

    private void showNotification(String title, String message, boolean closeWorkflowActivity) {
        new Notifications.NotificationBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(((AndroidUI) Setup.get().getUI()).getApplicationIcons().getAlert())
                .setId(1)
                .show();
        checkAndCloseWorkflowActivity(closeWorkflowActivity);
    }

    private void showDialog(Activity activity, String title, String message, final boolean closeWorkflowActivity) {
        alertLock.lock();
        try {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            alertDialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog = null;
                            checkAndCloseWorkflowActivity(closeWorkflowActivity);
                        }
                    })
                    .create();
            alertDialog.show();
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error("Stub!!", e);
        } finally {
            alertLock.unlock();
        }
    }

    private void checkAndCloseWorkflowActivity(boolean closeWorkflowActivity) {
        Activity activity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        if (closeWorkflowActivity && activity != null && !activity.isFinishing() && activity instanceof WorkflowActivity && !(activity instanceof JobsActivity)) {
            try {
                context.startActivity(new Intent(context, ServicesRegistry.getWorkflowService().getFirstActivity()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e) {
                MCLoggerFactory.getLogger(getClass()).error(e);
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        this.mediaPlayer = MediaPlayer.create(context, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.incoming_update));
    }
}