package com.magenta.mc.client.android.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.magenta.maxunits.mobile.entity.TaskState;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.rpc.operations.JobsRefreshing;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.events.EventType;
import com.magenta.mc.client.android.service.listeners.BroadcastEvent;
import com.magenta.mc.client.android.service.listeners.MxBroadcastEvents;
import com.magenta.mc.client.android.service.storage.DataControllerImpl;
import com.magenta.mc.client.android.service.storage.StateController;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;
import com.magenta.mc.client.android.ui.adapter.JobsAdapter;
import com.magenta.mc.client.android.util.IntentAttributes;
import com.magenta.mc.client.android.util.PhoneUtils;
import com.magenta.mc.client.client.DriverStatus;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.setup.Setup;

import java.util.List;

public class JobsActivity extends DistributionActivity implements WorkflowActivity {

    protected List<Job> list;
    ImageView callBtn;
    ArrayAdapter<Job> mAdapter;
    boolean showCompletedRun;

    public String getCustomTitle() {
        return getString(R.string.runs);
    }

    @MxBroadcastEvents({EventType.RELOAD_SCHEDULE, EventType.NEW_JOB, EventType.JOB_CANCELLED, EventType.JOB_UPDATED})
    public void onScheduleUpdate(BroadcastEvent<String> e) {
        refreshJobs(false);
    }

    @SuppressWarnings("unchecked")
    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        ListView listView = (ListView) findViewById(R.id.job_list);
        ImageView changeMapBtn = (ImageView) findViewById(R.id.change_map_btn);
        final ImageView viewBtn = (ImageView) findViewById(R.id.run_view_btn);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final ImageView refreshBtn = (ImageView) findViewById(R.id.run_refresh_btn);
        changeMapBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateMapSettings();
            }
        });
        list = ServicesRegistry.getDataController().loadCurrentJobs();
        mAdapter = new JobsAdapter(this, list);
        listView.setAdapter(mAdapter);
        listView.setTextFilterEnabled(true);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Job job = (Job) adapterView.getItemAtPosition(position);
                if (job != null) {
                    Intent intent;
                    boolean allowToPassJobsInArbitraryOrder = ((MxSettings) Setup.get().getSettings()).isAllowToPassJobsInArbitraryOrder();
                    boolean allowToPassStopsInArbitraryOrder = ((MxSettings) Setup.get().getSettings()).isAllowToPassStopsInArbitraryOrder();
                    boolean allPreviousRunIsComplete = true;
                    for (int i = 0; i < position; i++) {
                        Job j = (Job) adapterView.getItemAtPosition(i);
                        if (!j.isCompleted()) {
                            allPreviousRunIsComplete = false;
                            break;
                        }
                    }
                    int state = job.getState();
                    if (state == TaskState.RUN_RECEIVED && (allowToPassJobsInArbitraryOrder || position == 0)) {
                        intent = new Intent(JobsActivity.this, JobInfoActivity.class);
                    } else if (job.isCancelled() || job.isCompleted()) {
                        intent = new Intent(JobsActivity.this, ServicesRegistry.getWorkflowService().getJobActivity());
                    } else if (position != 0 && !(allPreviousRunIsComplete || (allowToPassJobsInArbitraryOrder && allowToPassStopsInArbitraryOrder))) {
                        intent = new Intent(JobsActivity.this, JobInfoActivity.class);
                        intent.putExtra(JobInfoActivity.HIDE_ACCEPT_AND_REJECT_BUTTONS, true);
                    } else {
                        intent = new Intent(JobsActivity.this, ServicesRegistry.getWorkflowService().getJobActivity());
                    }
                    intent.putExtra(IntentAttributes.JOB_ID, job.getReferenceId());
                    startActivity(intent);
                } else {
                    refreshJobs(true);
                }
            }
        });
        showCompletedRun = preferences.getBoolean("show_completed_run", false);
        changeIcon();
        viewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCompletedRun = !showCompletedRun;
                changeIcon();
                preferences.edit().putBoolean("show_completed_run", showCompletedRun).apply();
                viewBtn.invalidate();
                showRuns();
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JobsRefreshing.refresh();
                Animation animation = AnimationUtils.loadAnimation(JobsActivity.this, R.anim.refresh_anim);
                refreshBtn.startAnimation(animation);
            }
        });
        findViewById(R.id.run_allread_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (Job job : getJobs(true)) {
                    for (Stop stop : (List<Stop>) job.getStops()) {
                        stop.setUpdateType(Stop.NOT_CHANGED_STOP);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.logout_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logout();
            }
        });
        callBtn = (ImageView) findViewById(R.id.call_btn);
        updateDispatcherPhone();
        getDelegate().setDriverStatus(DriverStatus.getCurrent());
        showRuns();
    }

    private void showRuns() {
        ((JobsAdapter) mAdapter).changeList(getJobs(showCompletedRun));
        mAdapter.notifyDataSetChanged();
    }

    private void changeIcon() {
        float[] colorMatrix_Negative = {
                -1.0f, 0, 0, 0, 255,    //red
                0, -1.0f, 0, 0, 255,    //green
                0, 0, -1.0f, 0, 255,    //blue
                0, 0, 0, 1.0f, 0        //alpha
        };
        if (showCompletedRun) {
            ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negative);
            findViewById(R.id.run_view_btn).getBackground().setColorFilter(colorFilter_Negative);
        } else {
            findViewById(R.id.run_view_btn).getBackground().clearColorFilter();
        }
    }

    protected List<Job> getJobs(boolean showCompletedRun) {
        List<Job> jobs = ServicesRegistry.getDataController().loadCurrentJobs();
        if (showCompletedRun) {
            jobs.addAll(((DataControllerImpl) ServicesRegistry.getDataController()).getJobsFromHistory());
        }
        return jobs;
    }

    protected void onResume() {
        StateController.cleanCurrentJob();
        super.onResume();
        refreshJobs(true);
        updateDispatcherPhone();
    }

    private void updateDispatcherPhone() {
        String phone = ((MxSettings) Setup.get().getSettings()).getDispatcherPhoneNumber();
        PhoneUtils.assignPhone(callBtn, phone);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.refresh) {
            JobsRefreshing.refresh();
        } else if (i == R.id.logout) {
            logout();
        } else {
            return decorator.onMenuSelected(item) || super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void logout() {
        Login.getInstance().logout(true);
        ServicesRegistry.getDataController().clear();
        startActivity(new Intent(JobsActivity.this, LoginActivity.class));
        finish();
    }

    public void refreshJobs(boolean invokedFromActivity) {
        if (((AndroidUI) Setup.get().getUI()).getCurrentActivity() == this || invokedFromActivity) {
            getDelegate().getHandler().post(new Runnable() {
                @SuppressWarnings("unchecked")
                public void run() {
                    list.clear();
                    ServicesRegistry.getDataController().checkCancelledAndCompletedJobs();
                    list.addAll(ServicesRegistry.getDataController().loadCurrentJobs());
                    showRuns();
                }
            });
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}