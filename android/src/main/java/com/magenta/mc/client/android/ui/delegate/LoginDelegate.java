package com.magenta.mc.client.android.ui.delegate;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.UserStatus;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.HttpService;
import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.ui.activity.JobsActivity;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;
import com.magenta.mc.client.android.ui.dialog.DialogFactory;
import com.magenta.mc.client.android.ui.dialog.DistributionDialogFragment;

public class LoginDelegate extends HDDelegate {

    public void loginResult(int result) {
        MCLoggerFactory.getLogger(getClass()).debug("Login result = " + result);
        Activity activity = getActivity();
        if (activity instanceof LoginActivity) {
            switch (result) {
                case Constants.OK: {
                    McAndroidApplication.getInstance().setStatus(UserStatus.ONLINE);
                    ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.SETTINGS_TYPE));
                    ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.JOBS_TYPE));
                    ServiceHolder.getInstance().bindService(SocketIOService.class);
                    activity.startActivity(new Intent(activity, JobsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    activity.finish();
                    break;
                }
                case Constants.WARN:
                case Constants.ERROR: {
                    ((LoginActivity) activity).showLoginError();
                    break;
                }
            }
        }
    }

    public void settingsResult(int result) {
        // TODO: 2/13/17 return answer to UI
        MCLoggerFactory.getLogger(getClass()).debug("Login result = " + result);
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

    protected boolean onItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.mx_menu_login_properties) {
            ((LoginActivity) getActivity()).processSettingsButtonClick();
        } else if (i == R.id.mx_menu_login_exit) {
            getActivity().startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            getActivity().finish();
            MobileApp.getInstance().exit();
        }
        return super.onItemSelected(item);
    }
}
