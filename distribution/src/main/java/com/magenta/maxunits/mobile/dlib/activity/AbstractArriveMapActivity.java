package com.magenta.maxunits.mobile.dlib.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.activity.WorkflowActivity;
import com.magenta.maxunits.mobile.db.dao.SignatureDAO;
import com.magenta.maxunits.mobile.db.dao.StopsDAO;
import com.magenta.maxunits.mobile.dlib.controller.EmptyMapController;
import com.magenta.maxunits.mobile.dlib.controller.GoogleMapController;
import com.magenta.maxunits.mobile.dlib.controller.MapController;
import com.magenta.maxunits.mobile.dlib.controller.MapletController;
import com.magenta.maxunits.mobile.dlib.controller.YandexMapController;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.dlib.entity.DynamicAttributeType;
import com.magenta.maxunits.mobile.dlib.entity.MapSettingsEntity;
import com.magenta.maxunits.mobile.dlib.entity.OrderItemEntity;
import com.magenta.maxunits.mobile.dlib.mc.HDSettings;
import com.magenta.maxunits.mobile.dlib.record.OrderItemRecord;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Job;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.dlib.utils.Attribute;
import com.magenta.maxunits.mobile.dlib.utils.IntentAttributes;
import com.magenta.maxunits.mobile.dlib.view.DynamicAttributeView;
import com.magenta.maxunits.mobile.dlib.view.Maplet;
import com.magenta.maxunits.mobile.dlib.view.TimeView;
import com.magenta.maxunits.mobile.entity.AbstractJobStatus;
import com.magenta.maxunits.mobile.entity.TaskState;
import com.magenta.maxunits.mobile.mc.MxAndroidUtil;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.ui.map.MapAddress;
import com.magenta.maxunits.mobile.utils.StringUtils;
import com.magenta.mc.client.setup.Setup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractArriveMapActivity extends DistributionActivity implements WorkflowActivity {

    protected TextView timerField;
    protected Button arriveButton;
    protected Button abortBtn;
    protected Button pauseBtn;
    protected Button doneButton;
    protected boolean trackingEnabled;
    protected boolean trackingEnabledNewValue;
    protected boolean isPickup;
    protected Stop stop;
    protected Job job;
    protected MapController mapController;

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_arrive_map);
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

        List<MapSettingsEntity> entities;
        try {
            entities = DistributionDAO.getInstance(this).getMapSettings(Setup.get().getSettings().getLogin());
        } catch (SQLException ignore) {
            entities = new ArrayList<MapSettingsEntity>(0);
        }
        if (!entities.isEmpty() && mIsMapDisplayingEnabled) {
            switch (entities.get(0).getMapProviderType()) {
                case YANDEX: {
                    mapController = new YandexMapController(this, Collections.singletonList(stop), true);
                    break;
                }
                case GOOGLE: {
                    mapController = new GoogleMapController(this, Collections.singletonList(stop), true);
                    break;
                }
                default:
                    mapController = new MapletController(this, Collections.singletonList(stop), true);
            }
        } else {
            mapController = new EmptyMapController(this, Collections.singletonList(stop), true);
        }
        stop.setUpdateType(Stop.NOT_CHANGED_STOP);
        isPickup = stop.isPickup();
        trackingEnabled = MxSettings.getInstance().isMapTrackingEnabled();
        ((TextView) findViewById(R.id.date_value)).setText(StringUtils.formatDayAndMonth(stop.getDate()));
        ((TimeView) findViewById(R.id.time_value)).setTime(StringUtils.formatTime(stop.getDate()));
        setTimeWindow(stop);
        MapAddress destinationAddress = MapAddress.from(stop.getAddress());
        DynamicAttributeView dynamicAttributeView = new DynamicAttributeView(this, (TableLayout) findViewById(R.id.details))
                .add(new Attribute(getString(R.string.reference_label), stop.getStopName()))
                .add(new Attribute(getString(R.string.window), StringUtils.getTimeRange(stop.getParameterAsLong(Stop.ATTR_WINDOW_START_TIME, 0L), stop.getParameterAsLong(Stop.ATTR_WINDOW_END_TIME, 0L))))
                .add(new Attribute(getString(R.string.service), StringUtils.getTimeRange(stop.getDate().getTime() / 1000, stop.getParameterAsLong(Stop.ATTR_DEPART_TIME, stop.getDate().getTime() / 1000))))
                .add(new Attribute(getString(R.string.customer_label), stop.getCustomer()))
                .add(new Attribute(getString(R.string.customer_location), stop.getLocation()))
                .add(new Attribute(getString(R.string.address_label), stop.getAddressAsString()).addListeners(
                        new View.OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                return MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), AbstractArriveMapActivity.this);
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
            dynamicAttributeView.addAll(DistributionDAO.getInstance(getApplicationContext()).getDynamicAttributes(currentJobId, currentStopId));
        } catch (SQLException ignore) {
        } finally {
            dynamicAttributeView.clear().render();
        }
        startLateTimer(stop);
        if (destinationAddress != null) {
            ((TextView) findViewById(R.id.arrive_map_address_value)).setText(destinationAddress.getFull());
        }
        TextView customerField = (TextView) findViewById(R.id.arrive_map_customer_value);
        if (StringUtils.isBlank(stop.getCustomerInfo())) {
            customerField.setVisibility(View.GONE);
        } else {
            customerField.setVisibility(View.VISIBLE);
            customerField.setText(stop.getCustomerInfo());
        }
        RelativeLayout arriveMapHeaderView = (RelativeLayout) findViewById(R.id.arrive_map_header);
        arriveMapHeaderView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mapController.changeVisibility();
            }
        });
        arriveMapHeaderView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), AbstractArriveMapActivity.this);
            }
        });
        initButtons();
        updateButtonsState();
    }

    protected void initButtons() {
        arriveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (stop.getParameters().containsKey("customerLocationIsVerified") && MxAndroidUtil.getGeoLocation() != null && ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AbstractArriveMapActivity.this);
                    builder.setMessage(R.string.mx_msg_verify_location_question)
                            .setCancelable(false)
                            .setPositiveButton(R.string.mx_yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            stop.getStopValues().put("customerLocationIsVerified", "true");
                                            processStopArrived();
                                        }
                                    });
                    builder.setNegativeButton(R.string.mx_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    processStopArrived();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    processStopArrived();
                }
            }
        });
        abortBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(AbstractArriveMapActivity.this, AbortActivity.class)
                        .putExtra(IntentAttributes.JOB_ID, currentJobId)
                        .putExtra(IntentAttributes.STOP_ID, currentStopId));
            }
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                suspendStop();
            }
        });
    }

    private void processStopArrived() {
        stop.processSetState(TaskState.STOP_ARRIVED);
        updateButtonsState();
    }

    protected void initView() {
        timerField = (TextView) findViewById(R.id.timer);
        arriveButton = (Button) findViewById(R.id.arrive_button);
        abortBtn = (Button) findViewById(R.id.abort_button);
        pauseBtn = (Button) findViewById(R.id.pause_button);
        doneButton = (Button) findViewById(R.id.complete_button);
    }

    private void setTimeWindow(Stop stop) {
        String[] times = stop.getTimeWindowAsString().replaceAll(" ", "").split("[-,:]");
        ((TimeView) findViewById(R.id.left_bound)).setHours(times[0]).setMinutes(times[1]);
        ((TimeView) findViewById(R.id.right_bound)).setHours(times[2]).setMinutes(times[3]);
    }

    protected void suspendStop() {
        stop.processSetState(TaskState.STOP_SUSPENDED);
        startActivity(new Intent(AbstractArriveMapActivity.this, ServicesRegistry.getWorkflowService().getJobActivity()));
        finish();
    }

    public Integer getMenu() {
        return R.menu.fail_with_suspended;
    }

    public Integer getTitleBarLeftMenu() {
        return getMenu();
    }

    private void updateButtonsState() {
        if (currentJobId == null || currentStopId == null) {
            arriveButton.setVisibility(View.GONE);
            abortBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);
            return;
        }
        int lastState = new StopsDAO(this).getState(currentJobId, currentStopId);
        if (TaskState.STOP_ARRIVED == lastState) {
            arriveButton.setVisibility(View.GONE);
            abortBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.VISIBLE);
            doneButton.setText(getString(isPickup ? R.string.collection : R.string.delivery));
        } else {
            arriveButton.setVisibility(View.VISIBLE);
            abortBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);
        }
    }

    protected void completeJob(Job job, Stop stop) {
        try {
            List<OrderItemRecord> records = new LinkedList<OrderItemRecord>();
            for (OrderItemEntity entity : DistributionDAO.getInstance(AbstractArriveMapActivity.this).getOrderItems(currentJobId, currentStopId)) {
                records.add(entity.toRecord());
            }
            stop.setOrderItems(new Gson().toJson(records.toArray(new OrderItemRecord[records.size()])));
            DistributionDAO.getInstance(AbstractArriveMapActivity.this).clearOrderItems(stop.getReferenceId());
        } catch (SQLException ignore) {
        }
        Pair<String, String> signature = new SignatureDAO(AbstractArriveMapActivity.this).get(currentJobId, currentStopId);
        if (signature != null) {
            stop.setValue("name", signature.first);
            stop.setValue("signature", signature.second);
        }
        lockStatus(stop.processSetState(TaskState.STOP_COMPLETED));
        if (job.isCompleted() || job.stopsDone()) {
            startActivity(new Intent(AbstractArriveMapActivity.this, ServicesRegistry.getWorkflowService().getFirstActivity()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            finish();
        }
    }

    protected void lockStatus(AbstractJobStatus status) {
        //Your awesome code here
    }

    private void startLateTimer(Stop stop) {
        Date current = new Date();
        long millisFinished = stop.getDate().getTime() + 1000 * 60 * 30 - current.getTime();
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
                    timerField.setHighlightColor(0xfff00000);
                }
                String leftTime;
                if (days > 0) {
                    leftTime = String.format("%d %s %s%02d:%02d", days, getString(R.string.day), isLate ? "-" : "", hours, minutes);
                } else {
                    leftTime = String.format("%s%02d:%02d", isLate ? "-" : "", hours, minutes);
                }
                timerField.setText(leftTime);
            }

            public void onFinish() {
                timerField.setTextColor(0xfff00000);
                timerField.setHighlightColor(0xfff00000);
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

    protected void onStart() {
        super.onStart();
        mapController.onStart();
    }

    protected void onResume() {
        super.onResume();
        mapController.onResume();
    }

    protected void onPause() {
        super.onPause();
        mapController.onPause();
    }

    protected void onStop() {
        super.onStop();
        mapController.onStop();
    }

    public void onBackPressed() {
        finish();
    }

    public boolean onContextItemSelected(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Maplet.DIALOG_MAP_OPTIONS: {
                return new AlertDialog.Builder(this).setMultiChoiceItems(
                        new CharSequence[]{getString(R.string.mx_map_track_current_position)},
                        new boolean[]{trackingEnabled},
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                switch (which) {
                                    case 0:
                                        trackingEnabledNewValue = isChecked;
                                        break;
                                }
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                MxSettings.getInstance().setMapTrackingEnabled(trackingEnabled = trackingEnabledNewValue);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .create();
            }
            default:
                return super.onCreateDialog(id);
        }
    }
}