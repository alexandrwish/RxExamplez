package com.magenta.mc.client.android.ui.delegate;

import android.content.Intent;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.UserStatus;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;

public class WorkflowDelegate extends HDDelegate {

    public void jobsResult(int result) {
        MCLoggerFactory.getLogger(getClass()).debug("Jobs result = " + result);
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

    public void logout() {
        McAndroidApplication.getInstance().setStatus(UserStatus.LOGOUT);
        ServicesRegistry.getDataController().clear();
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}