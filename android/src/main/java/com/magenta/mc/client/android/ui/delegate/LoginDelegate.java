package com.magenta.mc.client.android.ui.delegate;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.util.Pair;
import android.view.MenuItem;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.binder.SocketBinder;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.UserStatus;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.listener.BindListener;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.HttpService;
import com.magenta.mc.client.android.service.LocationService;
import com.magenta.mc.client.android.service.SaveLocationsService;
import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.ui.activity.JobsActivity;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;

public class LoginDelegate extends HDDelegate {

    public void loginResult(int result) {
        MCLoggerFactory.getLogger(getClass()).debug("Login result = " + result);
        Activity activity = getActivity();
        if (activity instanceof LoginActivity) {
            switch (result) {
                case Constants.OK: {
                    HttpClient.getInstance().init();
                    McAndroidApplication.getInstance().setStatus(UserStatus.ONLINE);
                    activity.startActivity(new Intent(activity, JobsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    activity.finish();
                    ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.SETTINGS_TYPE));
                    ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.JOBS_TYPE));
                    ServiceHolder.getInstance().bindService(LocationService.class, new BindListener() {
                        public void onBind(IBinder binder) {
                            ServiceHolder.getInstance().startService(SaveLocationsService.class);
                        }
                    });
                    ServiceHolder.getInstance().bindService(SocketIOService.class, new BindListener() {
                        public void onBind(IBinder binder) {
                            ((SocketBinder) binder).subscribe();
                        }
                    });
                    break;
                }
                case Constants.WARN:
                    ((LoginActivity) activity).showLoginError(true);
                    break;
                case Constants.ERROR: {
                    ((LoginActivity) activity).showLoginError(false);
                    break;
                }
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
