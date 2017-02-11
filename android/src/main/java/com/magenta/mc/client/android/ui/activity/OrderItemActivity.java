package com.magenta.mc.client.android.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.OrderItemCheckStatus;
import com.magenta.mc.client.android.entity.OrderItemEntity;
import com.magenta.mc.client.android.entity.OrderItemStatus;
import com.magenta.mc.client.android.integrator.IntentIntegrator;
import com.magenta.mc.client.android.integrator.IntentResult;
import com.magenta.mc.client.android.sound.DSound;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.ui.adapter.ExpandableOrderItemAdapter;
import com.magenta.mc.client.android.util.DSoundPool;
import com.magenta.mc.client.android.util.StringUtils;
import com.magenta.mc.client.setup.Setup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderItemActivity extends DistributionActivity implements WorkflowActivity {

    public static final int REQUEST_CODE = OrderItemActivity.class.hashCode();
    public static final String EXTRA_BARCODE_LIST = "BARCODE_LIST";
    // Let's define some intent strings
    // This intent string contains the source of the data as a string
    static final String SOURCE_TAG = "com.motorolasolutions.emdk.datawedge.source";
    // This intent string contains the order_item_activity symbology as a string
    static final String LABEL_TYPE_TAG = "com.motorolasolutions.emdk.datawedge.label_type";
    // This intent string contains the order_item_activity data as a byte array list
    static final String DECODE_DATA_TAG = "com.motorolasolutions.emdk.datawedge.decode_data";
    // This intent string contains the captured data as a string
    // (in the case of MSR this data string contains a concatenation of the track data)
    static final String DATA_STRING_TAG = "com.motorolasolutions.emdk.datawedge.data_string";
    // Let's define the MSR intent strings (in case we want to use these in the future)
    static final String MSR_DATA_TAG = "com.motorolasolutions.emdk.datawedge.msr_data";
    static final String MSR_TRACK1_TAG = "com.motorolasolutions.emdk.datawedge.msr_track1";
    static final String MSR_TRACK2_TAG = "com.motorolasolutions.emdk.datawedge.msr_track2";
    static final String MSR_TRACK3_TAG = "com.motorolasolutions.emdk.datawedge.msr_track3";
    static final String MSR_TRACK1_STATUS_TAG = "com.motorolasolutions.emdk.datawedge.msr_track1_status";
    static final String MSR_TRACK2_STATUS_TAG = "com.motorolasolutions.emdk.datawedge.msr_track2_status";
    static final String MSR_TRACK3_STATUS_TAG = "com.motorolasolutions.emdk.datawedge.msr_track3_status";
    // Let's define the API intent strings for the soft scan trigger
    static final String ACTION_SOFTSCANTRIGGER = "com.motorolasolutions.emdk.datawedge.api.ACTION_SOFTSCANTRIGGER";
    static final String EXTRA_PARAM = "com.motorolasolutions.emdk.datawedge.api.EXTRA_PARAMETER";
    static final String DWAPI_START_SCANNING = "START_SCANNING";
    static final String DWAPI_STOP_SCANNING = "STOP_SCANNING";
    static final String DWAPI_TOGGLE_SCANNING = "TOGGLE_SCANNING";
    static final String ourIntentAction = "com.magenta.maxunits.mobile.hd.RECVR";
    List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
    EditText valueBarcode;
    EditText nameBarcode;
    LinearLayout header;
    ExpandableOrderItemAdapter adapter;
    DistributionDAO dao;

    public String getCustomTitle() {
        return getString(R.string.barcode_label);
    }

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_order_items);
        dao = DistributionDAO.getInstance(OrderItemActivity.this);
        try {
            orderItems.addAll(dao.getOrderItems(currentJobId, currentStopId));
        } catch (SQLException e) {
            finish();
        }
        adapter = new ExpandableOrderItemAdapter(this, orderItems);
        ListView listView = (ListView) findViewById(R.id.barcode_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.onClick(view, i);
            }
        });
        nameBarcode = (EditText) findViewById(R.id.name_barcode);
        nameBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        header = (LinearLayout) findViewById(R.id.barcode_header);
        header.setVisibility(View.GONE);
        valueBarcode = (EditText) findViewById(R.id.value_barcode);
        valueBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        valueBarcode.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                startScanner();
                return true;
            }
        });
        findViewById(R.id.add_barcode).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String barcode = valueBarcode.getText().toString();
                String name = nameBarcode.getText().toString();
                if (barcode.trim().isEmpty()) {
                    Toast.makeText(OrderItemActivity.this, R.string.empty_field, Toast.LENGTH_LONG).show();
                } else {
                    if (!checkBarcode(barcode).equals(OrderItemCheckStatus.FIND)) {
                        OrderItemEntity entity = new OrderItemEntity();
                        entity.setBarcode(barcode);
                        entity.setName(name);
                        entity.setJob(currentJobId);
                        entity.setStop(currentStopId);
                        entity.setStatus(OrderItemStatus.ADDED_BY_DRIVER);
                        entity.setMxID("-1");
                        try {
                            dao.createOrderItem(entity);
                        } catch (Exception ignore) {
                        }
                        orderItems.add(entity);
                        adapter.notifyDataSetChanged();
                    }
                    clearHeader(false);
                }
            }
        });
        findViewById(R.id.clear_barcode).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearHeader(false);
            }
        });
        findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startScanner();
            }
        });
        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearHeader(true);
            }
        });
        findViewById(R.id.give_over_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    int notScanned = 0;
                    for (OrderItemEntity orderItem : orderItems) {
                        if (orderItem.getStatus().equals(OrderItemStatus.NOT_CHECKED)) {
                            notScanned++;
                        }
                    }
                    if (notScanned > 0) {
                        new AlertDialog.Builder(OrderItemActivity.this)
                                .setMessage(String.format(getString(R.string.complete_barcode), (notScanned + "")))
                                .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        dialog.dismiss();
                                        OrderItemActivity.this.finish();
                                    }
                                })
                                .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        OrderItemActivity.this.finish();
                    }
                } catch (Exception ignore) {
                }
            }
        });
    }

    private OrderItemCheckStatus checkBarcode(String barcode) {
        boolean duplicate = false;
        if (!StringUtils.isBlank(barcode)) {
            for (OrderItemEntity item : orderItems) {
                if (item.getBarcode().trim().equalsIgnoreCase(barcode.trim())) {
                    if (item.getStatus().equals(OrderItemStatus.NOT_CHECKED)) {
                        item.setStatus(OrderItemStatus.CHECKED);
                        try {
                            dao.updateOrderItem(item);
                        } catch (Exception ignore) {
                        }
                        adapter.notifyDataSetChanged();
                        clearHeader(false);
                        DSoundPool.getInstance().playSound(DSound.SOUND_SUCCESS.getNum(), true);
                        return OrderItemCheckStatus.FIND;
                    } else {
                        duplicate = true;
                    }
                }
            }
        }
        valueBarcode.setText(barcode);
        if (duplicate) {
            Toast.makeText(OrderItemActivity.this, R.string.duplicate_barcode, Toast.LENGTH_LONG).show();
            DSoundPool.getInstance().playSound(DSound.SOUND_SUCCESS.getNum(), true);
            return OrderItemCheckStatus.DUPLICATE;
        }
        return OrderItemCheckStatus.INCORRECT;
    }

    private void startScanner() {
        if (Setup.get().getSettings().getBooleanProperty(MxSettings.MOTO_BARCODE, "false")) {
            OrderItemActivity.this.sendBroadcast(new Intent().setAction(ACTION_SOFTSCANTRIGGER).putExtra(EXTRA_PARAM, DWAPI_TOGGLE_SCANNING));
            Toast.makeText(this, "Soft scan trigger toggled.", Toast.LENGTH_SHORT).show();
            IntentFilter filter = new IntentFilter("com.magenta.maxunits.mobile.hd.RECVR");
            filter.addCategory("android.intent.category.DEFAULT");
            registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null && intent.getAction().contentEquals(ourIntentAction)) {
                        if (checkBarcode(intent.getStringExtra(DATA_STRING_TAG)).equals(OrderItemCheckStatus.INCORRECT) && (header.getVisibility() == View.GONE)) {
                            Toast.makeText(OrderItemActivity.this, R.string.wrong_barcode, Toast.LENGTH_LONG).show();
                            DSoundPool.getInstance().playSound(DSound.SOUND_ERROR.getNum(), true);
                        }
                    } else {
                        checkBarcode("");
                    }
                }
            }, filter);
        } else {
            IntentIntegrator integrator = new IntentIntegrator(OrderItemActivity.this);
            integrator.initiateScan();
        }
    }

    private void clearHeader(boolean visible) {
        header.setVisibility(visible ? View.VISIBLE : View.GONE);
        valueBarcode.setText("");
        nameBarcode.setText("");
    }

    public void removeOrderItem(OrderItemEntity entity) {
        for (Iterator<OrderItemEntity> iterator = orderItems.listIterator(); iterator.hasNext(); ) {
            OrderItemEntity orderItemEntity = iterator.next();
            if (orderItemEntity.getId() == entity.getId()) {
                iterator.remove();
                try {
                    dao.removeOrderItem(entity);
                } catch (Exception ignore) {
                }
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            if (checkBarcode(scanResult.getContents()).equals(OrderItemCheckStatus.INCORRECT) && header.getVisibility() == View.GONE) {
                Toast.makeText(OrderItemActivity.this, R.string.wrong_barcode, Toast.LENGTH_LONG).show();
                DSoundPool.getInstance().playSound(DSound.SOUND_ERROR.getNum(), true);
            }
        } else if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_CANCELED) {
            checkBarcode("");
        }
    }
}