package com.magenta.mc.client.android.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.mc.HDSettings;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.events.EventType;
import com.magenta.mc.client.android.service.events.JobEvent;
import com.magenta.mc.client.android.service.storage.DataControllerImpl;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.entity.TaskState;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.service.listeners.BroadcastEvent;
import com.magenta.mc.client.android.service.listeners.MxBroadcastEvents;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.adapter.StopsAdapter;
import com.magenta.mc.client.android.util.DateUtils;
import com.magenta.mc.client.android.util.IntentAttributes;
import com.magenta.mc.client.android.util.JobWorkflowUtils;
import com.magenta.mc.client.setup.Setup;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobActivity extends DistributionActivity implements WorkflowActivity {

    protected Job job;
    TableRow loadingRow;
    TableRow unloadingRow;
    TextView loading;
    TextView unloading;
    TextView pickupLocation;
    TextView dropLocation;
    TextView totalLoad;
    TextView route;
    ImageView allReadBtn;
    StopsAdapter mAdapter;
    boolean showCompletedJob;

    public String getCustomTitle() {
        return getString(R.string.job_activity_title);
    }

    @MxBroadcastEvents({EventType.NEW_JOB, EventType.JOB_CANCELLED, EventType.JOB_UPDATED})
    public void onScheduleUpdate(BroadcastEvent<String> e) {
        final Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (job != null && job.getReferenceId() != null && job.getReferenceId().equals(((JobEvent) e).getReferenceId())) {
            refreshStops(false);
            getDelegate().getHandler().post(new Runnable() {
                public void run() {
                    showRunInformation(job);
                }
            });
        }
    }

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_job);
        final TableLayout detailsLayout = (TableLayout) findViewById(R.id.details);
        final ImageView collapseImage = (ImageView) findViewById(R.id.collapse_img);
        ListView listView = (ListView) findViewById(R.id.stop_list);
        final ImageView viewBtn = (ImageView) findViewById(R.id.job_view_btn);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final ImageView showMapBtn = (ImageView) findViewById(R.id.job_map_btn);
        initView();
        Job j = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (j == null) {
            j = ((DataControllerImpl) ServicesRegistry.getDataController()).findFromHistoryJob(currentJobId);
            if (j == null) {
                finish();
                return;
            }
        }
        job = j;
        showRunInformation(job);
        showMapBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(JobActivity.this, MapActivity.class).putExtra(IntentAttributes.JOB_ID, job.getId()));
            }
        });
        pickupLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(JobActivity.this, MapActivity.class)
                        .putExtra(IntentAttributes.JOB_ID, job.getId())
                        .putExtra(IntentAttributes.LAT, job.getStartAddress().getLatitude())
                        .putExtra(IntentAttributes.LON, job.getStartAddress().getLongitude()));
            }
        });
        dropLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(JobActivity.this, MapActivity.class)
                        .putExtra(IntentAttributes.JOB_ID, job.getId())
                        .putExtra(IntentAttributes.LAT, job.getEndAddress().getLatitude())
                        .putExtra(IntentAttributes.LON, job.getEndAddress().getLongitude()));
            }
        });
        findViewById(R.id.header_panel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (detailsLayout.getVisibility() == View.GONE) {
                    detailsLayout.setVisibility(View.VISIBLE);
                    collapseImage.setImageResource(R.drawable.mc_icon_down_arrow);
                } else {
                    detailsLayout.setVisibility(View.GONE);
                    collapseImage.setImageResource(R.drawable.mc_icon_right_arrow);
                }
            }
        });
        mAdapter = new StopsAdapter(this, new ArrayList<>(job.getStops()));
        listView.setAdapter(mAdapter);
        listView.setTextFilterEnabled(true);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Stop stop = (Stop) adapterView.getItemAtPosition(position);
                if (stop.isCompleted()) return;
                JobWorkflowUtils.openNextActivity(stop, job, JobActivity.this);
            }
        });
        showCompletedJob = preferences.getBoolean("show_completed_job", false);
        changeIcon();
        viewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCompletedJob = !showCompletedJob;
                changeIcon();
                preferences.edit().putBoolean("show_completed_job", showCompletedJob).apply();
                viewBtn.invalidate();
                showJobs();
            }
        });
        allReadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (Stop stop : (List<Stop>) job.getStops()) {
                    stop.setUpdateType(Stop.NOT_CHANGED_STOP);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        ((LinearLayout.LayoutParams) findViewById(R.id.state_layout).getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
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
        allReadBtn = (ImageView) findViewById(R.id.job_allread_btn);
    }

    protected void updateStatistic() {
        int failed = 0;
        int complete = 0;
        int total = 0;
        int suspend = 0;
        for (Stop stop : (List<Stop>) job.getStops()) {
            total++;
            if (stop.isCancelled()) {
                failed++;
            } else if (stop.isCompleted()) {
                complete++;
            } else if (stop.getState() == TaskState.STOP_SUSPENDED) {
                suspend++;
            }
        }
        ((TextView) findViewById(R.id.total_jobs)).setText(" " + total + " ");
        ((TextView) findViewById(R.id.failed_jobs)).setText(" " + failed + " ");
        ((TextView) findViewById(R.id.complete_jobs)).setText(" " + complete + " ");
        ((TextView) findViewById(R.id.suspended_jobs)).setText(" " + suspend + " ");
        ((TextView) findViewById(R.id.jobs)).setText(" " + (total - failed - complete - suspend));
    }

    private void showJobs() {
        mAdapter.update(getStops(showCompletedJob));
    }

    private List<Stop> getStops(boolean showCompletedJob) {
        List<Stop> stops = new ArrayList<>();
        for (Stop stop : (List<Stop>) job.getStops()) {
            if (showCompletedJob) {
                stops.add(stop);
            } else if (!stop.isCompleted() && (!(job.isCompleted() || job.isCancelled()))) {
                stops.add(stop);
            }
        }
        return stops;
    }

    private void changeIcon() {
        float[] colorMatrix_Negative = {
                -1.0f, 0, 0, 0, 255,    //red
                0, -1.0f, 0, 0, 255,    //green
                0, 0, -1.0f, 0, 255,    //blue
                0, 0, 0, 1.0f, 0        //alpha
        };
        if (showCompletedJob) {
            ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negative);
            findViewById(R.id.job_view_btn).getBackground().setColorFilter(colorFilter_Negative);
        } else {
            findViewById(R.id.job_view_btn).getBackground().clearColorFilter();
        }
    }

    private void showRunInformation(Job job) {
        pickupLocation.setText(Html.fromHtml("<u> " + job.getStartAddress().getFullAddress() + " </u>"));
        dropLocation.setText(Html.fromHtml("<u> " + job.getEndAddress().getFullAddress() + " </u>"));
        long loadingEndTime = job.getParameterAsInt(Job.ATTR_LOADING_END_TIME, 0);
        long loadingDuration = job.getParameterAsInt(Job.ATTR_LOADING_DURATION, 0);
        long unloadingEndTime = job.getParameterAsInt(Job.ATTR_UNLOADING_END_TIME, 0);
        long unloadingDuration = job.getParameterAsInt(Job.ATTR_UNLOADING_DURATION, 0);
        double totalLoad = job.getParameterAsDouble(Job.ATTR_TOTAL_LOAD, 0);
        double totalVolume = job.getParameterAsDouble(Job.ATTR_TOTAL_VOLUME, 0);
        long totalDrivingTime = job.getParameterAsLong(Job.ATTR_DRIVING_TIME, 0);
        double totalDistance = job.getParameterAsDouble(Job.ATTR_TOTAL_DISTANCE, 0);
        if (loadingDuration > 0) {
            loading.setText(DateUtils.toStringTime(new Date((loadingEndTime - loadingDuration) * 1000))
                    + " - " + DateUtils.toStringTime(new Date(loadingEndTime * 1000)));
            loadingRow.setVisibility(View.VISIBLE);
        } else {
            loadingRow.setVisibility(View.GONE);
        }
        if (unloadingDuration > 0) {
            unloading.setText(DateUtils.toStringTime(new Date((unloadingEndTime - unloadingDuration) * 1000))
                    + " - " + DateUtils.toStringTime(new Date(unloadingEndTime * 1000)));
            unloadingRow.setVisibility(View.VISIBLE);
        } else {
            unloadingRow.setVisibility(View.GONE);
        }
        NumberFormat format = new DecimalFormat("#.#####");
        if (totalLoad > 0 || totalVolume > 0) {
            this.totalLoad.setText(String.format("%s %s / %s %s",
                    format.format(totalLoad), MxSettings.get().getProperty(HDSettings.MX_CONFIG_CAPACITY_UNITS, ""),
                    format.format(totalVolume), MxSettings.get().getProperty(HDSettings.MX_CONFIG_VOLUME_UNIT, "")));
        } else {
            this.totalLoad.setText("");
        }
        if (totalDrivingTime > 0 || totalDistance > 0) {
            route.setText(String.format("%s %s / %s",
                    format.format(totalDistance), getString(R.string.km),
                    DateUtils.timeToHumanReadable((int) totalDrivingTime, this)));
        } else {
            route.setText("");
        }
    }

    protected void onResume() {
        super.onResume();
        refreshStops(true);
        updateStatistic();
        allReadBtn.setVisibility(job.isCancelled() || job.isCompleted() ? View.GONE : View.VISIBLE);
    }

    public void onBackPressed() {
        startActivity(new Intent(JobActivity.this, ServicesRegistry.getWorkflowService().getFirstActivity()).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void refreshStops(boolean invokedFromActivity) {
        if (((AndroidUI) Setup.get().getUI()).getCurrentActivity() == this || invokedFromActivity) {
            getDelegate().getHandler().post(new Runnable() {
                public void run() {
                    Job j = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
                    if (j == null) {
                        j = ((DataControllerImpl) ServicesRegistry.getDataController()).findFromHistoryJob(currentJobId);
                        if (j == null) {
                            finish();
                            return;
                        }
                    }
                    job = j;
                    showJobs();
                }
            });
        }
    }
}