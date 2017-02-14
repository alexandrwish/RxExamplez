package com.magenta.mc.client.android.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.DynamicAttributeType;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.mc.HDSettings;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.ui.view.DynamicAttributeView;
import com.magenta.mc.client.android.util.Attribute;
import com.magenta.mc.client.android.util.DistributionUtils;
import com.magenta.mc.client.android.util.IntentAttributes;
import com.magenta.mc.client.android.util.StringUtils;

import java.sql.SQLException;

public class StartActivity extends DistributionActivity implements WorkflowActivity {

    public static String DISABLE_START_BUTTON = "hideStartButton";

    protected boolean fromNavigator = false;
    protected Button startButton;
    protected Button resumeButton;
    protected Button abortButton;

    public String getCustomTitle() {
        Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (job != null) {
            Stop stop = (Stop) job.getStop(currentStopId);
            if (stop != null) {
                return getString(R.string.run) + ": " + job.getParameter("number");
            }
        }
        return null;
    }

    public Integer getMenu() {
        return R.menu.fail;
    }

    public Integer getTitleBarLeftMenu() {
        return getMenu();
    }

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        Pair<Job, Stop> pair = ServicesRegistry.getDataController().find(currentJobId, currentStopId);
        if (pair == null) {
            finish();
            return;
        }
        final Stop stop = pair.second;
        stop.setUpdateType(Stop.NOT_CHANGED_STOP);
        DynamicAttributeView dynamicAttributeView = new DynamicAttributeView(this, (TableLayout) findViewById(R.id.details))
                .add(new Attribute(getString(R.string.reference_label), stop.getStopName()))
                .add(new Attribute(getString(R.string.stop_type), getString(stop.isPickup() ? R.string.collection : R.string.delivery)).setIcon(DistributionUtils.getStopPriorityIcon(StartActivity.this, stop)).setType(DynamicAttributeType.IMAGE))
                .add(new Attribute(getString(R.string.window), StringUtils.getTimeRange(stop.getParameterAsLong(Stop.ATTR_WINDOW_START_TIME, 0L), stop.getParameterAsLong(Stop.ATTR_WINDOW_END_TIME, 0L))))
                .add(new Attribute(getString(R.string.service), StringUtils.getTimeRange(stop.getDate().getTime() / 1000, stop.getParameterAsLong(Stop.ATTR_DEPART_TIME, stop.getDate().getTime() / 1000))))
                .add(new Attribute(getString(R.string.customer_label), stop.getCustomer()))
                .add(new Attribute(getString(R.string.customer_location), stop.getLocation()))
                .add(new Attribute(getString(R.string.address_label), stop.getAddressAsString()).addListeners(
                        new View.OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                return MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), StartActivity.this);
                            }
                        }))
                .add(new Attribute(getString(R.string.load), StringUtils.formatDouble(stop.getParameter(Stop.ATTR_LOAD))).setUnit(MxSettings.get().getProperty(HDSettings.MX_CONFIG_CAPACITY_UNITS, "")))
                .add(new Attribute(getString(R.string.volume), StringUtils.formatDouble(stop.getParameter(Stop.ATTR_VOLUME))).setUnit(MxSettings.get().getProperty(HDSettings.MX_CONFIG_CAPACITY_UNITS, "")))
                .add(new Attribute(getString(R.string.contact_label), stop.getContactPerson()))
                .add(new Attribute(getString(R.string.phone_label), stop.getContactPhone()).setType(DynamicAttributeType.PHONE))
                .add(new Attribute(getString(R.string.cost_label), StringUtils.formatCost(stop.getParameter(Stop.ATTR_COST))))
                .add(new Attribute(getString(R.string.order_type_label), stop.getParameter(Stop.ATTR_ORDER_TYPE)))
                .add(new Attribute(getString(R.string.notes_label), stop.getNotes()));
        try {
            dynamicAttributeView.addAll(DistributionDAO.getInstance().getDynamicAttributes(currentJobId, currentStopId));
        } catch (SQLException ignore) {
        } finally {
            dynamicAttributeView.clear().render();
        }
        updateButtons();
    }

    protected void initView() {
        startButton = (Button) findViewById(R.id.start_button);
        resumeButton = (Button) findViewById(R.id.resume_button);
        abortButton = (Button) findViewById(R.id.abort_button);
    }

    protected void onResume() {
        super.onResume();
        if (!fromNavigator) {
            updateButtons();
        } else {
            goToArriveMapActivity();
        }
    }

    protected void updateButtons() {
        Pair<Job, Stop> pair = ServicesRegistry.getDataController().find(currentJobId, currentStopId);
        if (pair == null || pair.second == null) {
            finish();
            return;
        }
        Stop stop = pair.second;
        if (stop.getState() == TaskState.STOP_SUSPENDED) {
            startButton.setVisibility(View.GONE);
            resumeButton.setVisibility(View.VISIBLE);
            setResumeBtnOnClickListener(stop);
        } else {
            startButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility(View.GONE);
            setStartButtonOnClickListener(stop);
        }
        setAbortBtnOnClickListener();
    }

    protected void setAbortBtnOnClickListener() {
        abortButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, AbortActivity.class)
                        .putExtra(IntentAttributes.JOB_ID, currentJobId)
                        .putExtra(IntentAttributes.STOP_ID, currentStopId));
                finish();
            }
        });
    }

    protected void setResumeBtnOnClickListener(final Stop stop) {
        resumeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Stop startedStop = getStartedStop(stop);
                        if (startedStop == null) {
                            processStop(stop);
                        } else {
                            new AlertDialog.Builder(StartActivity.this)
                                    .setMessage(R.string.suspend_other_job)
                                    .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            startedStop.processSetState(TaskState.STOP_SUSPENDED);
                                            processStop(stop);
                                        }
                                    })
                                    .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    }
                });
            }
        });
    }

    protected void setStartButtonOnClickListener(final Stop stop) {
        final Boolean disableBtn = getIntent().getExtras() != null && getIntent().getExtras().getBoolean(DISABLE_START_BUTTON, false);
        startButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (disableBtn) {
                    Setup.get().getUI().getDialogManager().asyncMessageSafe(getString(R.string.info), getString(R.string.complete_previous));
                } else {
                    final Stop startedStop = getStartedStop(stop);
                    if (startedStop == null) {
                        processStop(stop);
                    } else {
                        new AlertDialog.Builder(StartActivity.this)
                                .setMessage(R.string.suspend_other_job)
                                .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startedStop.processSetState(TaskState.STOP_SUSPENDED);
                                        processStop(stop);
                                    }
                                })
                                .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }
        });
    }

    protected Stop getStartedStop(Stop stop) {
        for (Object o : stop.getParentJob().getStops()) {
            Stop s = (Stop) o;
            if (s.isProcessing() && !s.isCompleted()) {
                return s;
            }
        }
        return null;
    }

    protected void processStop(final Stop stop) {
        stop.processSetState(TaskState.STOP_ON_ROUTE);
        runOnUiThread(new Runnable() {
            public void run() {
                new AlertDialog.Builder(StartActivity.this)
                        .setMessage(R.string.launch_navi_app)
                        .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fromNavigator = MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), StartActivity.this);
                            }
                        })
                        .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                goToArriveMapActivity();
                            }
                        }).show();
            }
        });
    }

    public boolean onContextItemSelected(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    protected void goToArriveMapActivity() {
        startActivity(new Intent(StartActivity.this, ServicesRegistry.getWorkflowService().getArrivedActivity())
                .putExtra(IntentAttributes.JOB_ID, currentJobId)
                .putExtra(IntentAttributes.STOP_ID, currentStopId));
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.abort) {
            startActivity(new Intent(this, AbortActivity.class)
                    .putExtra(IntentAttributes.JOB_ID, currentJobId)
                    .putExtra(IntentAttributes.STOP_ID, currentStopId));
        } else if (i == R.id.fail) {
            startActivity(new Intent(this, AbortActivity.class)
                    .putExtra(AbortActivity.EXTRA_APPLY_FOR_RUN, true)
                    .putExtra(IntentAttributes.JOB_ID, currentJobId)
                    .putExtra(IntentAttributes.STOP_ID, currentStopId));
        } else {
            return decorator.onMenuSelected(item) || super.onOptionsItemSelected(item);
        }
        return true;
    }
}