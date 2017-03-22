package com.magenta.mc.client.android.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.db.dao.SignatureDAO;
import com.magenta.mc.client.android.entity.DynamicAttributeEntity;
import com.magenta.mc.client.android.entity.DynamicAttributeType;
import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.mc.MxAndroidUtil;
import com.magenta.mc.client.android.record.DynamicAttributeRecord;
import com.magenta.mc.client.android.record.OrderItemRecord;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.ui.adapter.ViewGridAdapter;
import com.magenta.mc.client.android.ui.map.MapAddress;
import com.magenta.mc.client.android.ui.view.DynamicAttributeView;
import com.magenta.mc.client.android.ui.view.TimeView;
import com.magenta.mc.client.android.util.Attribute;
import com.magenta.mc.client.android.util.DistributionUtils;
import com.magenta.mc.client.android.util.StringUtils;
import com.magenta.mc.client.android.util.TextFilter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CompleteActivity extends DistributionActivity {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM", Locale.UK);
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.UK);
    TextView timerField;
    Stop stop;
    Job job;

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_complete);
        job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (job == null) {
            finish();
            return;
        }
        stop = (Stop) job.getStop(currentStopId);
        if (stop == null) {
            finish();
            return;
        }
        initView();
        ((TextView) findViewById(R.id.date_value)).setText(simpleDateFormat.format(stop.getDate()));
        ((TimeView) findViewById(R.id.time_value)).setTime(simpleTimeFormat.format(stop.getDate()));
        setTimeWindow(stop);
        MapAddress destinationAddress = MapAddress.from(stop.getAddress());
        DynamicAttributeView dynamicAttributeView = new DynamicAttributeView(this, (TableLayout) findViewById(R.id.details))
                .add(new Attribute(getString(R.string.reference_label), stop.getStopName()))
                .add(new Attribute(getString(R.string.stop_type), getString(stop.isPickup() ? R.string.collection : R.string.delivery)).setIcon(DistributionUtils.getStopPriorityIcon(CompleteActivity.this, stop)).setType(DynamicAttributeType.IMAGE))
                .add(new Attribute(getString(R.string.window), StringUtils.getTimeRange(stop.getParameterAsLong(Stop.ATTR_WINDOW_START_TIME, 0L), stop.getParameterAsLong(Stop.ATTR_WINDOW_END_TIME, 0L))))
                .add(new Attribute(getString(R.string.service), StringUtils.getTimeRange(stop.getDate().getTime() / 1000, stop.getParameterAsLong(Stop.ATTR_DEPART_TIME, stop.getDate().getTime() / 1000))))
                .add(new Attribute(getString(R.string.customer_label), stop.getCustomer()))
                .add(new Attribute(getString(R.string.customer_location), stop.getLocation()))
                .add(new Attribute(getString(R.string.address_label), stop.getAddressAsString()).addListeners(
                        new View.OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                return MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), CompleteActivity.this);
                            }
                        }))
                .add(new Attribute(getString(R.string.load), StringUtils.formatDouble(stop.getParameter(Stop.ATTR_LOAD))).setUnit(Settings.get().getCapacityUnit()))
                .add(new Attribute(getString(R.string.volume), StringUtils.formatDouble(stop.getParameter(Stop.ATTR_VOLUME))).setUnit(Settings.get().getCapacityUnit()))
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
        startLateTimer(stop);
        if (destinationAddress != null) {
            ((TextView) findViewById(R.id.arrive_map_address_value)).setText(destinationAddress.getFull());
        }
        TextView customerField = (TextView) findViewById(R.id.arrive_map_customer_value);
        if (StringUtils.isBlank(stop.getCustomer())) {
            customerField.setVisibility(View.GONE);
        } else {
            customerField.setVisibility(View.VISIBLE);
            customerField.setText(stop.getCustomer());
        }
        RelativeLayout arriveMapHeaderView = (RelativeLayout) findViewById(R.id.complete_header);
        arriveMapHeaderView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), CompleteActivity.this);
            }
        });
    }

    protected void initView() {
        timerField = (TextView) findViewById(R.id.timer);
        List<Button> btnList = new LinkedList<>();
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        if (Settings.get().getFactCost()) {
            Button costButton = new Button(this);
            costButton.setLayoutParams(params);
            costButton.setText(R.string.cost);
            costButton.setId(R.string.cost + 201412);
            costButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    View view = getLayoutInflater().inflate(R.layout.view_fact_cost, null);
                    final EditText costField = (EditText) view.findViewById(R.id.fact_cost);
                    costField.setFilters(new InputFilter[]{new TextFilter(TextFilter.TextFilterType.MONEY, 6)});
                    costField.setText(stop.getFactCost());
                    new AlertDialog.Builder(CompleteActivity.this)
                            .setView(view)
                            .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String cost = costField.getText().toString();
                                    if (!StringUtils.isBlank(cost)) {
                                        stop.setFactCost(cost);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.mx_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
            btnList.add(costButton);
        }
        if (Settings.get().getBarcodeScreen()) {
            Button scanButton = new Button(this);
            scanButton.setLayoutParams(params);
            scanButton.setText(R.string.scan);
            scanButton.setId(R.string.scan + 201412);
            scanButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(CompleteActivity.this, OrderItemActivity.class).putExtra(IntentAttributes.JOB_ID, currentJobId).putExtra(IntentAttributes.STOP_ID, currentStopId));
                }
            });
            btnList.add(scanButton);
        }
        if (Settings.get().getSignatureScreen()) {
            Button pobButton = new Button(this);
            pobButton.setLayoutParams(params);
            pobButton.setText(R.string.pod);
            pobButton.setId(R.string.pod + 201412);
            pobButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Object[] signature = new SignatureDAO(CompleteActivity.this).get(currentJobId, currentStopId);
                    Intent intent = new Intent(CompleteActivity.this, SignatureActivity.class);
                    if (signature != null) {
                        intent.putExtra(SignatureActivity.EXTRA_CONTACT_NAME, (String) signature[0])
                                .putExtra(SignatureActivity.EXTRA_SIGNATURE, (String) signature[1])
                                .putExtra(SignatureActivity.EXTRA_SIGNATURE_TIMESTAMP, Long.valueOf((String) signature[2]));
                    }
                    startActivityForResult(intent, SignatureActivity.REQUEST_CODE);
                }
            });
            btnList.add(pobButton);
        }
        Button doneButton = new Button(this);
        doneButton.setLayoutParams(params);
        doneButton.setText(R.string.done);
        doneButton.setId(R.string.done + 201412);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(CompleteActivity.this)
                        .setMessage(R.string.complete_job_question)
                        .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                completeJob(job, stop);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        btnList.add(doneButton);
        GridView buttonGrid = (GridView) findViewById(R.id.button_grid);
        buttonGrid.setNumColumns(btnList.size() < 4 ? btnList.size() : GridView.AUTO_FIT);
        buttonGrid.setAdapter(new ViewGridAdapter<>(btnList));
    }

    private void setTimeWindow(Stop stop) {
        String[] times = stop.getTimeWindowAsString().replaceAll(" ", "").split("[-,:]");
        ((TimeView) findViewById(R.id.left_bound)).setHours(times[0]).setMinutes(times[1]);
        ((TimeView) findViewById(R.id.right_bound)).setHours(times[2]).setMinutes(times[3]);
    }

    private void suspendStop() {
        stop.processSetState(TaskState.STOP_SUSPENDED);
        startActivity(new Intent(CompleteActivity.this, JobActivity.class));
        finish();
    }

    public Integer getMenu() {
        return R.menu.fail_with_suspended;
    }

    public Integer getTitleBarLeftMenu() {
        return getMenu();
    }

    private void completeJob(Job job, Stop stop) {
        try {
            List<DynamicAttributeRecord> records = new LinkedList<>();
            for (DynamicAttributeEntity entity : DistributionDAO.getInstance().getDynamicAttributes(currentJobId, currentStopId)) {
                if (entity.isPdaEditable() && entity.isPdaRequired() && StringUtils.isBlank(entity.getValue()) && !entity.getTypeName().equals(DynamicAttributeType.BOOLEAN)) {
                    Toast.makeText(this, R.string.fill_all_attributes, Toast.LENGTH_LONG).show();
                    return;
                }
                records.add(entity.toRecord());
            }
            stop.setDynamicAttributes(new Gson().toJson(records.toArray(new DynamicAttributeRecord[records.size()])));
            DistributionDAO.getInstance().clearDynamicAttribute(stop.getReferenceId());
        } catch (SQLException ignore) {
        }
        try {
            List<OrderItemRecord> records = new LinkedList<>();
            for (OrderItemEntity entity : DistributionDAO.getInstance().getOrderItems(currentJobId, currentStopId)) {
                records.add(entity.toRecord());
            }
            stop.setOrderItems(new Gson().toJson(records.toArray(new OrderItemRecord[records.size()])));
            DistributionDAO.getInstance().clearOrderItems(stop.getReferenceId());
        } catch (SQLException ignore) {
        }
        String[] signature = new SignatureDAO(CompleteActivity.this).get(currentJobId, currentStopId);
        if (signature != null) {
            stop.setValue("name", signature[0]);
            stop.setValue("signature", signature[1]);
            stop.setValue("signature_time", signature[2]);
        }
        stop.processSetState(TaskState.STOP_COMPLETED);
        if (job.isCompleted() || job.stopsDone()) {
            startActivity(new Intent(CompleteActivity.this, JobsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            startActivity(new Intent(CompleteActivity.this, JobActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(IntentAttributes.JOB_ID, currentJobId).putExtra(IntentAttributes.STOP_ID, currentStopId));
        }
    }

    private void startLateTimer(Stop stop) {
        Date current = new Date();
        Date orderDate = stop.getDate();
        long millisFinished = orderDate.getTime() + 1000 * 60 * 30 - current.getTime();
        new CountDownTimer(millisFinished, 1000 * 60) {
            public void onTick(long millisUntilFinished) {
                long diff = millisUntilFinished - 1000 * 60 * 30;
                boolean isLate = diff < 0;
                diff = Math.abs(diff);
                int minutes = (int) ((diff / (1000 * 60)) % 60);
                int hours = (int) ((diff / (1000 * 60 * 60)) % 24);
                int days = (int) (diff / (1000 * 60 * 60 * 24) % 30);
                if (isLate) {
                    timerField.setTextColor(0xfff00000);
                }
                String leftTime;
                if (days > 0) {
                    leftTime = String.format(Locale.UK, "%d DAY(s) %s%02d:%02d", days, isLate ? "-" : "", hours, minutes);
                } else {
                    leftTime = String.format(Locale.UK, "%s%02d:%02d", isLate ? "-" : "", hours, minutes);
                }
                timerField.setText(leftTime);
            }

            public void onFinish() {
                timerField.setTextColor(0xfff00000);
                timerField.setText(getString(R.string.late));
            }
        }.start();
    }

    public String getCustomTitle() {
        Job job = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (job != null) {
            Stop stop = (Stop) job.getStop(currentStopId);
            if (stop != null) {
                return getString(R.string.run) + ": " + job.getParameter("number") + " "
                        + getString(R.string.stop) + ": " + stop.getStopName() + " "
                        + getString(R.string.status) + ": " + stop.getStateString();
            }
        }
        return null;
    }

    public void onBackPressed() {
        finish();
    }

    public boolean onContextItemSelected(MenuItem item) {
        return onOptionsItemSelected(item);
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
        } else if (i == R.id.suspend) {
            suspendStop();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (SignatureActivity.REQUEST_CODE == requestCode) {
            String signature = data.getStringExtra(SignatureActivity.EXTRA_SIGNATURE);
            String contactName = data.getStringExtra(SignatureActivity.EXTRA_CONTACT_NAME);
            Long time = data.getLongExtra(SignatureActivity.EXTRA_SIGNATURE_TIMESTAMP, 0);
            new SignatureDAO(this).update(currentJobId, currentStopId, contactName, signature, time);
        }
    }
}