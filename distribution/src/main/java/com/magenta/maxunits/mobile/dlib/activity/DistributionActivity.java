package com.magenta.maxunits.mobile.dlib.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.activity.GenericActivity;
import com.magenta.maxunits.mobile.dlib.dialog.DialogFactory;
import com.magenta.maxunits.mobile.dlib.dialog.DistributionDialogFragment;
import com.magenta.maxunits.mobile.dlib.service.events.AlertEvent;
import com.magenta.maxunits.mobile.dlib.service.events.EventType;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Job;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.dlib.utils.IntentAttributes;
import com.magenta.maxunits.mobile.events.InstallUpdateEvent;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.service.listeners.BroadcastEvent;
import com.magenta.maxunits.mobile.service.listeners.MxBroadcastEvents;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.setup.Setup;

import java.util.HashMap;

public abstract class DistributionActivity extends GenericActivity<HDActivityDecorator> {

    protected String currentJobId;
    protected String currentStopId;
    protected boolean mIsMapDisplayingEnabled;
    protected MxSettings mSettings;
    private boolean isUpdateDialogShowed = false;
    private boolean isDialogShowedAfterLoseFocus = false;
    private String updateFilePathFromEvent;

    {
        decorator = (HDActivityDecorator) ServicesRegistry.getWorkflowService().getDecorator(this);
    }

    public void initActivity(Bundle savedInstanceState) {
        currentJobId = getIntent().getStringExtra(IntentAttributes.JOB_ID);
        if (currentJobId == null && savedInstanceState != null) {
            currentJobId = savedInstanceState.getString(IntentAttributes.JOB_ID);
        }
        currentStopId = getIntent().getStringExtra(IntentAttributes.STOP_ID);
        if (currentStopId == null && savedInstanceState != null) {
            currentStopId = savedInstanceState.getString(IntentAttributes.STOP_ID);
        }
        mSettings = ((MxSettings) Setup.get().getSettings());
        mIsMapDisplayingEnabled = mSettings.isMapDisplayingEnabled();
        if (savedInstanceState != null) {
            isUpdateDialogShowed = savedInstanceState.getBoolean(IntentAttributes.UPDATE_DIALOG_SHOWED);
            updateFilePathFromEvent = savedInstanceState.getString(IntentAttributes.UPDATE_DIALOG_PATH);
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            isUpdateDialogShowed = preferences.getBoolean(IntentAttributes.UPDATE_DIALOG_SHOWED, isUpdateDialogShowed);
            updateFilePathFromEvent = preferences.getString(IntentAttributes.UPDATE_DIALOG_PATH, updateFilePathFromEvent);
        }
        if (isUpdateDialogShowed && updateFilePathFromEvent != null) {
            showUpdateDialog(updateFilePathFromEvent);
        }
    }

    protected void initView() {
        //init views after setContentView(R.id.layout)
    }

    public boolean onContextItemSelected(MenuItem item) {
        return decorator.onMenuSelected(item) || super.onContextItemSelected(item);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isUpdateDialogShowed && updateFilePathFromEvent != null && !isDialogShowedAfterLoseFocus) {
            showUpdateDialog(updateFilePathFromEvent);
            isDialogShowedAfterLoseFocus = true;
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentJobId = savedInstanceState.getString(IntentAttributes.JOB_ID);
        currentStopId = savedInstanceState.getString(IntentAttributes.STOP_ID);
        isUpdateDialogShowed = savedInstanceState.getBoolean(IntentAttributes.UPDATE_DIALOG_SHOWED);
        updateFilePathFromEvent = savedInstanceState.getString(IntentAttributes.UPDATE_DIALOG_PATH);
        isDialogShowedAfterLoseFocus = savedInstanceState.getBoolean(IntentAttributes.DIALOG_SHOWED_AFTER_LOSE_FOCUS);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(IntentAttributes.JOB_ID, currentJobId);
        outState.putString(IntentAttributes.STOP_ID, currentStopId);
        outState.putBoolean(IntentAttributes.UPDATE_DIALOG_SHOWED, isUpdateDialogShowed);
        outState.putString(IntentAttributes.UPDATE_DIALOG_PATH, updateFilePathFromEvent);
        outState.putBoolean(IntentAttributes.DIALOG_SHOWED_AFTER_LOSE_FOCUS, isDialogShowedAfterLoseFocus);
        super.onSaveInstanceState(outState);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return decorator.onMenuSelected(item) || super.onContextItemSelected(item);
    }

    public String getCustomTitle() {
        Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (job != null) {
            Stop stop = (Stop) job.getStop(currentStopId);
            if (stop != null) {
                return getString(R.string.run) + ": " + job.getParameter("number") + " " + getString(R.string.stop) + ": " + stop.getStopName() + " " + getString(R.string.status) + ": " + stop.getStateString();
            }
        }
        return null;
    }

    @MxBroadcastEvents({EventType.PERFORMER_ALERT})
    public void onAlert(BroadcastEvent<String> e) {
        decorator.showAlert((AlertEvent) e);
    }

    @MxBroadcastEvents({EventType.INSTALL_UPDATE})
    public void onInstallUpdate(BroadcastEvent<String> e) {
        updateFilePathFromEvent = ((InstallUpdateEvent) e).getPath();
        showUpdateDialog(updateFilePathFromEvent);
    }

    public void updateMapSettings() {
        HashMap mapSettings = new Gson().fromJson((String) Setup.get().getSettings().get("map.property"), HashMap.class);
        if (mapSettings != null && mapSettings.size() > 1) {
            Bundle bundle = new Bundle();
            bundle.putInt(DialogFactory.TITLE, R.string.map_dialog);
            bundle.putSerializable("map.property", mapSettings);
            DistributionDialogFragment fragment = DialogFactory.create(DialogFactory.MAP_CHOOSER_DIALOG, bundle);
            fragment.show(getFragmentManager(), fragment.getName());
        }
    }

    private void showUpdateDialog(final String path) {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(DistributionActivity.this)
                        .setMessage(!mSettings.isUpdateDelayed() ? R.string.update_needed_text : R.string.update_needed_second_text)
                        .setPositiveButton(R.string.mx_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                resetRestoreUpdateData();
                                final Bundle bundle = new Bundle(3);
                                bundle.clear();
                                bundle.putBoolean("update", true);
                                bundle.putString("path", path);
                                Login.getInstance().logout(true);
                                ServicesRegistry.getWorkflowService().logout(bundle);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false);
                if (!mSettings.isUpdateDelayed()) {
                    builder.setNegativeButton(R.string.mx_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetRestoreUpdateData();
                            mSettings.setUpdateDelayed(true);
                            Toast.makeText(DistributionActivity.this, R.string.update_needed_toast_text, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                if (!mSettings.isUpdateDelayed()) {
                    builder.setNegativeButton(R.string.mx_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            resetRestoreUpdateData();
                            mSettings.setUpdateDelayed(true);
                            Toast.makeText(DistributionActivity.this, R.string.update_needed_toast_text, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                saveDialogStateToPref();
                isUpdateDialogShowed = true;
                builder.show();
            }
        });
    }

    private void resetRestoreUpdateData() {
        isDialogShowedAfterLoseFocus = false;
        isUpdateDialogShowed = false;
        saveDialogStateToPref();
    }

    private void saveDialogStateToPref() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences
                .edit()
                .putBoolean(IntentAttributes.UPDATE_DIALOG_SHOWED, isUpdateDialogShowed)
                .putString(IntentAttributes.UPDATE_DIALOG_PATH, updateFilePathFromEvent)
                .apply();
    }
}