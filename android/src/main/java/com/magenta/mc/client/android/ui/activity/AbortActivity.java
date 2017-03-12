package com.magenta.mc.client.android.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.record.DynamicAttributeRecord;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.listeners.BroadcastEvent;
import com.magenta.mc.client.android.service.listeners.MxBroadcastEvents;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbortActivity extends DistributionActivity implements WorkflowActivity {

    public static final String EXTRA_APPLY_FOR_RUN = "APPLY_FOR_RUN";
    private Job job;
    private Stop stop;
    private ArrayAdapter<String> mReasonsAdapter;
    private ArrayList<String> mReasons;

    @MxBroadcastEvents({"CANCEL_REASONS_UPDATE"})
    public void onScheduleUpdate(BroadcastEvent<String> e) {
        updateReasons();
    }

    private void updateReasons() {
        mReasons.clear();
        if (!MxSettings.getInstance().isOfflineVersion()) {
            mReasons.addAll(MxSettings.getInstance().getOrderCancelReasons());
        }
        if (mReasons.isEmpty()) {
            mReasons.addAll(Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.pickup_abort_code_array)));
        }
        mReasonsAdapter.notifyDataSetChanged();
    }

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_abort);
        if (ServicesRegistry.getDataController().findJob(currentJobId) == null) {
            finish();
            return;
        }
        final boolean isApplyForRun = getIntent().getBooleanExtra(EXTRA_APPLY_FOR_RUN, false);
        final EditText comment = (EditText) findViewById(R.id.comment);
        job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (!isApplyForRun && job != null) {
            stop = (Stop) job.getStop(currentStopId);
        }
        mReasons = new ArrayList<>();
        mReasonsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_cancel_reason, mReasons);
        mReasonsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_cancel_reason);
        final Spinner spinner = (Spinner) findViewById(R.id.code);
        spinner.setAdapter(mReasonsAdapter);
        updateReasons();
        findViewById(R.id.abort_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(AbortActivity.this)
                        .setMessage(R.string.abort_job_question)
                        .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int which) {
                                Intent intent = null;
                                if (job != null) {
                                    if (isApplyForRun) {
                                        Map<String, String> parameters = new HashMap<>(2);
                                        parameters.put("abort-stop-code", spinner.getSelectedItem().toString());
                                        parameters.put("comment", comment.getText().toString());
                                        Map<String, String> attrMap = new HashMap<>();
                                        for (AbstractStop stop : job.getStops()) {
                                            try {
                                                List<DynamicAttributeRecord> records = new LinkedList<>();
                                                for (DynamicAttributeEntity entity : DistributionDAO.getInstance().getDynamicAttributes(currentJobId, currentStopId)) {
                                                    records.add(entity.toRecord());
                                                }
                                                String stopAttr = new Gson().toJson(records.toArray(new DynamicAttributeRecord[records.size()]));
                                                DistributionDAO.getInstance().clearDynamicAttribute(stop.getReferenceId());
                                                attrMap.put(stop.getReferenceId(), stopAttr);
                                            } catch (SQLException ignore) {
                                            }
                                        }
                                        parameters.put("stopAttributes", new Gson().toJson(attrMap));
                                        job.processSetState(TaskState.RUN_ABORTED, true, parameters);
                                        Toast.makeText(AbortActivity.this, getString(R.string.run_failed), Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (stop != null) {
                                            try {
                                                List<DynamicAttributeRecord> records = new LinkedList<>();
                                                for (DynamicAttributeEntity entity : DistributionDAO.getInstance().getDynamicAttributes(currentJobId, currentStopId)) {
                                                    records.add(entity.toRecord());
                                                }
                                                stop.setDynamicAttributes(new Gson().toJson(records.toArray(new DynamicAttributeRecord[records.size()])));
                                                DistributionDAO.getInstance().clearDynamicAttribute(stop.getReferenceId());
                                            } catch (SQLException ignore) {
                                            }
                                            stop.setValue("abort-stop-code", StringUtils.encodeURI(spinner.getSelectedItem().toString()));
                                            stop.setValue("comment", StringUtils.encodeURI(comment.getText().toString()));//URLEncoder.encode(comment.getText().toString()));
                                            stop.processSetState(TaskState.STOP_FAIL);
                                            Toast.makeText(AbortActivity.this, getString(R.string.aborted), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    intent = new Intent(AbortActivity.this, !job.isCancelled() && !job.isCompleted() ? JobActivity.class : JobsActivity.class).putExtra(IntentAttributes.JOB_ID, currentJobId);
                                }
                                startActivity(intent != null ? intent : new Intent(AbortActivity.this, JobsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }
}