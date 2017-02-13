package com.magenta.mc.client.android.ui.delegate;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.receiver.HDReceiver;
import com.magenta.mc.client.android.receiver.HttpResponseListener;
import com.magenta.mc.client.android.ui.dialog.DialogFactory;
import com.magenta.mc.client.android.ui.dialog.DistributionDialogFragment;
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

    public void settingsResult(int result) {
        // TODO: 2/13/17 return answer to UI
        MCLoggerFactory.getLogger(HDDelegate.class).debug("Login result = " + result);
        switch (result) {
            case Constants.OK: {
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
}