package com.magenta.mc.client.android.ui.delegate;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.listener.HttpResponseListener;
import com.magenta.mc.client.android.receiver.HDReceiver;
import com.magenta.mc.client.android.service.SenderService;

public class HDDelegate extends SmokeActivityDelegate implements HttpResponseListener {

    protected PendingIntent intent;
    private BroadcastReceiver hdReceiver;
    private IntentFilter intentFilter;

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
    }

    public void settingsResult(int result) {
    }

    public void jobsResult(int result) {
    }
}