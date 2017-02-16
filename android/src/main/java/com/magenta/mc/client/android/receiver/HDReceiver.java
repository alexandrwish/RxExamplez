package com.magenta.mc.client.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.listener.HttpResponseListener;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.common.IntentAttributes;

public class HDReceiver extends BroadcastReceiver {

    private HttpResponseListener listener;

    public HDReceiver(HttpResponseListener listener) {
        this.listener = listener;
    }

    public void onReceive(Context context, Intent intent) {
        MCLoggerFactory.getLogger(HDReceiver.class).debug("Receive message");
        if (intent.getExtras().containsKey(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE)) {
            listener.loginResult(intent.getIntExtra(IntentAttributes.HTTP_LOGIN_RESPONSE_TYPE, 0));
        } else if (intent.getExtras().containsKey(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE)) {
            listener.settingsResult(intent.getIntExtra(IntentAttributes.HTTP_SETTINGS_RESPONSE_TYPE, 0));
        } else if(intent.getExtras().containsKey(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE)) {
            listener.jobsResult(intent.getIntExtra(IntentAttributes.HTTP_JOBS_RESPONSE_TYPE, 0));
        }
    }
}