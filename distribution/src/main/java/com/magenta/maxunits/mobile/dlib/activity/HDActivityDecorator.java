package com.magenta.maxunits.mobile.dlib.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.activity.ActivityDecorator;
import com.magenta.maxunits.mobile.activity.common.LoginActivity;
import com.magenta.maxunits.mobile.dlib.service.events.AlertEvent;
import com.magenta.maxunits.mobile.dlib.utils.IntentAttributes;
import com.magenta.maxunits.mobile.rpc.operations.LogoutLock;
import com.magenta.maxunits.mobile.service.ServicesRegistry;

import java.util.List;

public class HDActivityDecorator extends ActivityDecorator {

    AlertDialog alertDialog;

    public HDActivityDecorator(Context context) {
        super(context);
    }

    public boolean onMenuSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            LogoutLock.getInstance().logout();
            context.startActivity(new Intent(context, LoginActivity.class));
        }
        return true;
    }

    public void showAlert(final AlertEvent event) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(context)
                    .setMessage(R.string.performer_alert)
                    .setPositiveButton(R.string.mx_show, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<String> jobs = event.getJobs();
                            List<String> runs = event.getRuns();
                            Intent intent;
                            if (runs.size() == 1) {
                                if (jobs.size() == 1) {
                                    intent = new Intent(context, ServicesRegistry.getWorkflowService().getStartActivity()).putExtra(IntentAttributes.STOP_ID, jobs.get(0));
                                } else {
                                    intent = new Intent(context, ServicesRegistry.getWorkflowService().getJobActivity());
                                }
                                intent.putExtra(IntentAttributes.JOB_ID, runs.get(0));
                            } else {
                                intent = new Intent(context, ServicesRegistry.getWorkflowService().getFirstActivity());
                            }
                            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    })
                    .setNegativeButton(R.string.mx_ignore, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //ignore
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialogInterface) {
                            //ignore
                        }
                    })
                    .show();
        } else if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }
}