package com.magenta.mc.client.android.ui.delegate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Pair;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.listener.HttpResponseListener;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.receiver.HDReceiver;
import com.magenta.mc.client.android.service.HttpService;
import com.magenta.mc.client.android.service.SenderService;
import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.ui.dialog.DialogFactory;
import com.magenta.mc.client.android.ui.dialog.DistributionDialogFragment;

public class HDDelegate extends SmokeActivityDelegate implements HttpResponseListener {

    private BroadcastReceiver hdReceiver;
    private IntentFilter intentFilter;
    private PendingIntent intent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = PendingIntent.getService(McAndroidApplication.getInstance(), 16022017, new Intent(McAndroidApplication.getInstance(), SenderService.class), 0);
        intentFilter = new IntentFilter(Constants.HTTP_SERVICE_NAME);
        hdReceiver = new HDReceiver(this);
    }

    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(hdReceiver, intentFilter);
    }

    public void onStop() {
        getActivity().unregisterReceiver(hdReceiver);
        super.onStop();
    }

    public void loginResult(int result) {
        // TODO: 2/13/17 return answer to UI
        MCLoggerFactory.getLogger(HDDelegate.class).debug("Login result = " + result);
        switch (result) {
            case Constants.OK: {
                ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.SETTINGS_TYPE));
                ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.JOBS_TYPE));
                ServiceHolder.getInstance().bindService(SocketIOService.class);
                break;
            }
            case Constants.WARN: {
                break;
            }
            case Constants.ERROR: {
                break;
            }
        }
    }

    public void settingsResult(int result) {
        // TODO: 2/13/17 return answer to UI
        MCLoggerFactory.getLogger(HDDelegate.class).debug("Login result = " + result);
        switch (result) {
            case Constants.OK: {
                ((AlarmManager) McAndroidApplication.getInstance().getSystemService(Context.ALARM_SERVICE))
                        .setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Constants.SEND_DELTA, Constants.SEND_DELTA, intent);
                break;
            }
            case Constants.WARN: {
                Bundle bundle = new Bundle(3);
                bundle.putInt(DialogFactory.ICON, android.R.drawable.ic_dialog_info);
                bundle.putInt(DialogFactory.TITLE, R.string.alert_map_title);
                bundle.putInt(DialogFactory.VALUE, R.string.alert_map_value);
                DistributionDialogFragment fragment = DialogFactory.create(DialogFactory.ALERT_DIALOG, bundle);
                if (fragment != null) {
                    fragment.show(getActivity().getFragmentManager(), fragment.getName());
                }
                break;
            }
            case Constants.ERROR: {
                break;
            }
        }
    }

    public void jobsResult(int result) {
        // TODO: 2/13/17 return answer to UI
        MCLoggerFactory.getLogger(HDDelegate.class).debug("Jobs result = " + result);
        switch (result) {
            case Constants.START: {
                break;
            }
            case Constants.ERROR: {
                break;
            }
            case Constants.STOP: {
                break;
            }
        }
    }
}