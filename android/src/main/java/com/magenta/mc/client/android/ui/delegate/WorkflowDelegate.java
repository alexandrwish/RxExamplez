package com.magenta.mc.client.android.ui.delegate;

import android.content.Intent;
import android.os.IBinder;
import android.util.Pair;
import android.view.MenuItem;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.binder.SocketBinder;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.UserStatus;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.service.HttpService;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.SocketIOService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.ui.activity.AbortActivity;
import com.magenta.mc.client.android.ui.activity.AbstractArriveMapActivity;
import com.magenta.mc.client.android.ui.activity.DistributionActivity;
import com.magenta.mc.client.android.ui.activity.JobActivity;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;

public class WorkflowDelegate extends HDDelegate {

    public void onResume() {
        super.onResume();
        IBinder binder = ServiceHolder.getInstance().getService(SocketIOService.class.getName());
        if (binder != null) {
            ((SocketBinder) binder).subscribe(this);
        }
    }

    public void onPause() {
        super.onPause();
        IBinder binder = ServiceHolder.getInstance().getService(SocketIOService.class.getName());
        if (binder != null) {
            ((SocketBinder) binder).unsubscribe();
        }
    }

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

    public void reloadJobs() {
        ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.JOBS_TYPE));
    }

    public void suspendStop() {
        if (getActivity() instanceof AbstractArriveMapActivity) {
            ((AbstractArriveMapActivity)getActivity()).getStop().processSetState(TaskState.STOP_SUSPENDED);
            getActivity().startActivity(new Intent(getActivity(), JobActivity.class));
            getActivity().finish();
        }
    }

    public void abortStop() {
        getActivity().startActivity(new Intent(getActivity(), AbortActivity.class)
                .putExtra(IntentAttributes.JOB_ID, ((DistributionActivity)getActivity()).getCurrentJobId())
                .putExtra(IntentAttributes.STOP_ID, ((DistributionActivity)getActivity()).getCurrentStopId()));
    }

    protected boolean onItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.abort) {
            abortStop();
        } else if (i == R.id.fail) {
            getActivity().startActivity(new Intent(getActivity(), AbortActivity.class)
                    .putExtra(AbortActivity.EXTRA_APPLY_FOR_RUN, true)
                    .putExtra(IntentAttributes.JOB_ID, ((DistributionActivity) getActivity()).getCurrentJobId())
                    .putExtra(IntentAttributes.STOP_ID, ((DistributionActivity) getActivity()).getCurrentStopId()));
        } else if (i == R.id.suspend) {
            if (getActivity() instanceof AbstractArriveMapActivity) {
                suspendStop();
            }
        } else if (i == R.id.refresh) {
            reloadJobs();
        } else if (i == R.id.logout) {
            logout();
        }
        return super.onItemSelected(item);
    }

}