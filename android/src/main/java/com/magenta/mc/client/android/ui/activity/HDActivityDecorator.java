package com.magenta.mc.client.android.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.events.AlertEvent;

import java.util.List;

public class HDActivityDecorator extends ActivityDecorator {

    private AlertDialog alertDialog;

    public HDActivityDecorator(Context context) {
        super(context);
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
                                    intent = new Intent(context, StartActivity.class).putExtra(IntentAttributes.STOP_ID, jobs.get(0));
                                } else {
                                    intent = new Intent(context, JobActivity.class);
                                }
                                intent.putExtra(IntentAttributes.JOB_ID, runs.get(0));
                            } else {
                                intent = new Intent(context, JobsActivity.class);
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