package com.magenta.mc.client.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.Job;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.events.EventType;
import com.magenta.mc.client.android.events.JobEvent;
import com.magenta.mc.client.android.listener.BroadcastEvent;
import com.magenta.mc.client.android.listener.MxBroadcastEvents;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.setup.Setup;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.adapter.JobDetailStopsAdapter;
import com.magenta.mc.client.android.util.DateUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

public class JobInfoActivity extends DistributionActivity implements WorkflowActivity {

    public static final String HIDE_ACCEPT_AND_REJECT_BUTTONS = "HIDE_ACCEPT_AND_REJECT_BUTTONS";

    TableRow loadingRow;
    TableRow unloadingRow;
    TextView loading;
    TextView unloading;
    TextView pickupLocation;
    TextView dropLocation;
    TextView totalLoad;
    TextView route;
    JobDetailStopsAdapter stopsAdapter;

    public String getCustomTitle() {
        return getString(R.string.job_activity_title);
    }

    @SuppressWarnings("unused")
    @MxBroadcastEvents({EventType.NEW_JOB, EventType.JOB_CANCELLED, EventType.JOB_UPDATED})
    public void onScheduleUpdate(BroadcastEvent<String> e) {
        final Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (job != null && job.getReferenceId() != null && job.getReferenceId().equals(((JobEvent) e).getReferenceId())) {
            refreshInfo(false);
            getDelegate().getHandler().post(new Runnable() {
                public void run() {
                    showRunInformation(job);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_job_info);
        initView();
        Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (job == null) {
            finish();
            return;
        }
        stopsAdapter = new JobDetailStopsAdapter(this, new ArrayList<AbstractStop>());
        ((ListView) findViewById(R.id.stop_list)).setAdapter(stopsAdapter);
        refreshInfo(true);
        findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
                if (job != null) {
                    job.processSetState(TaskState.RUN_ACCEPTED);
                    startActivity(new Intent(JobInfoActivity.this, JobActivity.class)
                            .putExtra(IntentAttributes.JOB_ID, currentJobId)
                            .putExtra(IntentAttributes.STOP_ID, currentStopId));
                    finish();
                }
            }
        });
        findViewById(R.id.reject_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
                if (job != null) {
                    job.processSetState(TaskState.RUN_REJECTED);
                    startActivity(new Intent(JobInfoActivity.this, JobsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });
        processAcceptAndRejectButtonsVisibility();
    }

    protected void initView() {
        loading = (TextView) findViewById(R.id.loading);
        unloading = (TextView) findViewById(R.id.unloading);
        pickupLocation = (TextView) findViewById(R.id.pickup_location);
        dropLocation = (TextView) findViewById(R.id.drop_location);
        loadingRow = (TableRow) findViewById(R.id.loading_row);
        unloadingRow = (TableRow) findViewById(R.id.unloading_row);
        totalLoad = (TextView) findViewById(R.id.total_load);
        route = (TextView) findViewById(R.id.route);
    }

    protected void onResume() {
        super.onResume();
        processAcceptAndRejectButtonsVisibility();
    }

    public void onBackPressed() {
        startActivity(new Intent(JobInfoActivity.this, JobsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    void processAcceptAndRejectButtonsVisibility() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(HIDE_ACCEPT_AND_REJECT_BUTTONS, false)) {
            findViewById(R.id.job_info_button_panel).setVisibility(View.GONE);
        } else {
            findViewById(R.id.job_info_button_panel).setVisibility(View.VISIBLE);
        }
    }

    void refreshInfo(boolean invokedFromActivity) {
        if (((AndroidUI) Setup.get().getUI()).getCurrentActivity() == this || invokedFromActivity) {
            getDelegate().getHandler().post(new Runnable() {
                @SuppressWarnings("unchecked")
                public void run() {
                    Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
                    if (job != null) {
                        showRunInformation(job);
                        stopsAdapter.update(job.getStops());
                    }
                }
            });
        }
    }

    void showRunInformation(Job job) {
        pickupLocation.setText(job.getStartAddress().getFullAddress());
        dropLocation.setText(job.getEndAddress().getFullAddress());
        long loadingEndTime = job.getParameterAsLong(Job.ATTR_LOADING_END_TIME, 0);
        long loadingDuration = job.getParameterAsLong(Job.ATTR_LOADING_DURATION, 0);
        long unloadingEndTime = job.getParameterAsLong(Job.ATTR_UNLOADING_END_TIME, 0);
        long unloadingDuration = job.getParameterAsLong(Job.ATTR_UNLOADING_DURATION, 0);
        double totalLoad = job.getParameterAsDouble(Job.ATTR_TOTAL_LOAD, 0);
        double totalVolume = job.getParameterAsDouble(Job.ATTR_TOTAL_VOLUME, 0);
        long totalDrivingTime = job.getParameterAsLong(Job.ATTR_DRIVING_TIME, 0);
        double totalDistance = job.getParameterAsDouble(Job.ATTR_TOTAL_DISTANCE, 0);
        if (loadingDuration > 0) {
            loading.setText(DateUtils.toStringTime(new Date((loadingEndTime - loadingDuration)))
                    + " - " + DateUtils.toStringTime(new Date(loadingEndTime)));
            loadingRow.setVisibility(View.VISIBLE);
        } else {
            loadingRow.setVisibility(View.GONE);
        }
        if (unloadingDuration > 0) {
            unloading.setText(DateUtils.toStringTime(new Date(unloadingEndTime - unloadingDuration))
                    + " - " + DateUtils.toStringTime(new Date(unloadingEndTime)));
            unloadingRow.setVisibility(View.VISIBLE);
        } else {
            unloadingRow.setVisibility(View.GONE);
        }
        NumberFormat format = new DecimalFormat("#.#####");
        if (totalLoad > 0 || totalVolume > 0) {
            this.totalLoad.setText(String.format("%s %s / %s %s", format.format(totalLoad), Settings.get().getCapacityUnit(), format.format(totalVolume), Settings.get().getVolumeUnit()));
        } else {
            this.totalLoad.setText("");
        }
        if (totalDrivingTime > 0 || totalDistance > 0) {
            route.setText(String.format("%s %s / %s", format.format(totalDistance), getString(R.string.km), DateUtils.timeToHumanReadable((int) totalDrivingTime, this)));
        } else {
            route.setText("");
        }
    }
}