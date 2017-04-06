package com.magenta.mc.client.android.ui.delegate;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.R;
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
import com.magenta.mc.client.android.ui.activity.JobsActivity;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;
import com.magenta.mc.client.android.ui.dialog.DialogFactory;
import com.magenta.mc.client.android.ui.dialog.DistributionDialogFragment;

public class WorkflowDelegate extends HDDelegate {

    private DistributionDialogFragment waitIconDialog;

    public void jobsResult(int result) {
        MCLoggerFactory.getLogger(getClass()).debug("Jobs result = " + result);
        switch (result) {
            case Constants.START: {
                if (waitIconDialog == null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(DialogFactory.TITLE, R.string.info);
                    bundle.putInt(DialogFactory.VALUE, R.string.please_wait);
                    waitIconDialog = DialogFactory.create(DialogFactory.WAIT_DIALOG, bundle);
                }
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                Fragment prev = getActivity().getFragmentManager().findFragmentByTag(waitIconDialog.getName());
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                waitIconDialog.show(ft, waitIconDialog.getName());
                break;
            }
            case Constants.ERROR: {
                if (waitIconDialog != null) {
                    waitIconDialog.dismiss();
                }
                break;
            }
            case Constants.STOP: {
                if (waitIconDialog != null) {
                    waitIconDialog.dismiss();
                }
                if (getActivity() instanceof JobsActivity) {
                    ((JobsActivity) getActivity()).refreshJobs(false);
                }
                break;
            }
        }
    }

    public void settingsResult(int result) {
        // TODO: 2/13/17 return answer to UI
        MCLoggerFactory.getLogger(getClass()).debug("Login result = " + result);
        ((AlarmManager) McAndroidApplication.getInstance().getSystemService(Context.ALARM_SERVICE))
                .setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Constants.SEND_DELTA, Constants.SEND_DELTA, intent);
        switch (result) {
            case Constants.NEED_UPDATE: {
                if (getActivity() instanceof DistributionActivity) {
                    ((DistributionActivity) getActivity()).updateMapSettings();
                }
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

    public void logout() {
        McAndroidApplication.getInstance().setStatus(UserStatus.LOGOUT);
        ServicesRegistry.getDataController().clear();
        ServiceHolder.getInstance().stopService(SocketIOService.class);
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    public void reloadJobs() {
        ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.JOBS_TYPE));
    }

    public void suspendStop() {
        if (getActivity() instanceof AbstractArriveMapActivity) {
            ((AbstractArriveMapActivity) getActivity()).getStop().processSetState(TaskState.STOP_SUSPENDED);
            getActivity().startActivity(new Intent(getActivity(), JobActivity.class));
            getActivity().finish();
        }
    }

    public void abortStop() {
        getActivity().startActivity(new Intent(getActivity(), AbortActivity.class)
                .putExtra(IntentAttributes.JOB_ID, ((DistributionActivity) getActivity()).getCurrentJobId())
                .putExtra(IntentAttributes.STOP_ID, ((DistributionActivity) getActivity()).getCurrentStopId()));
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