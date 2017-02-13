package com.magenta.mc.client.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.util.IntentAttributes;
import com.magenta.mc.client.log.MCLoggerFactory;

public class HDReceiver extends BroadcastReceiver {

    private HttpResponseListener listener;

    public HDReceiver(HttpResponseListener listener) {
        this.listener = listener;
    }

    public void onReceive(Context context, Intent intent) {
        MCLoggerFactory.getLogger(HDReceiver.class).debug("Receive message");
        if (intent.getExtras().containsKey(IntentAttributes.HTTP_RESPONSE_TYPE)) {
            listener.loginResult(intent.getIntExtra(IntentAttributes.HTTP_RESPONSE_TYPE, 0));
        }
    }
}