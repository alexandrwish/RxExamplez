package com.magenta.mc.client.android.ui.delegate;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;

import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.receiver.HDReceiver;
import com.magenta.mc.client.android.receiver.HttpResponseListener;
import com.magenta.mc.client.log.MCLoggerFactory;

public class HDDelegate extends SmokeActivityDelegate implements HttpResponseListener {

    private BroadcastReceiver hdReceiver;
    private IntentFilter intentFilter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}