package com.magenta.mc.client.android.ui.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.Job;
import com.magenta.mc.client.android.entity.Stop;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.handler.MapUpdateHandler;
import com.magenta.mc.client.android.receiver.RouteUpdateReceiver;
import com.magenta.mc.client.android.service.HttpService;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.ui.activity.AbortActivity;
import com.magenta.mc.client.android.ui.activity.ArriveMapActivity;
import com.magenta.mc.client.android.util.DateUtils;
import com.magenta.mc.client.android.util.JobWorkflowUtils;
import com.magenta.mc.client.android.util.MxAndroidUtil;
import com.magenta.mc.client.android.util.PhoneUtils;
import com.magenta.mc.client.android.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class MapController implements View.OnClickListener {

    public boolean mTrackCurrentPosition;
    protected Activity mActivity;
    protected LinearLayout mHolder;
    protected MapUpdateHandler mHandler;
    protected List<AbstractStop> mStops;
    protected boolean routeWithDriver;
    protected RouteUpdateReceiver routeUpdateReceiver;
    protected Long synchronizeTimestamp;
    protected ArrayList<Address> addresses;
    protected AlertDialog mDialog;
    protected View mBaseMapView;
    protected ImageButton mBtnZoomIn, mBtnZoomOut, mBtnMyLocation;

    public MapController(Activity activity, List<AbstractStop> stops, boolean routeWithDriver) {
        mActivity = activity;
        mBaseMapView = activity.getLayoutInflater().inflate(R.layout.view_map, null);
        LinearLayout parentHolder = (LinearLayout) activity.findViewById(R.id.map_controller);
        mBtnZoomIn = (ImageButton) mBaseMapView.findViewById(R.id.zoom_in);
        mBtnZoomOut = (ImageButton) mBaseMapView.findViewById(R.id.zoom_out);
        mBtnMyLocation = (ImageButton) mBaseMapView.findViewById(R.id.my_location);
        parentHolder.addView(mBaseMapView);
        mHolder = (LinearLayout) mBaseMapView.findViewById(R.id.hldr);
        mHolder.removeAllViews();
        mStops = stops;
        this.routeWithDriver = routeWithDriver;
        this.routeUpdateReceiver = new RouteUpdateReceiver(this);
        this.addresses = new ArrayList<>();
        mBtnZoomIn.setOnClickListener(this);
        mBtnZoomOut.setOnClickListener(this);
        mBtnMyLocation.setOnClickListener(this);
    }

    public void onMapReady() {
        List<Address> addresses = new ArrayList<>();
        for (AbstractStop stop : mStops) {
            addresses.add(stop.getAddress());
        }
        fitBounds(addresses);
    }

    public void fitBounds(List<Address> address) {

    }

    public void onStart() {
        synchronizeTimestamp = System.currentTimeMillis();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentAttributes.UPDATE_ROUTE_ACTION);
        mActivity.registerReceiver(routeUpdateReceiver, filter);
    }

    public void onResume() {
        if (mHolder.isShown() && mHandler != null) {
            mHandler.start();
        }
    }

    public void onPause() {
        if (mHolder.isShown() && mHandler != null) {
            mHandler.stop();
        }
    }

    public void changeVisibility() {
        if (mBaseMapView.isShown()) {
            mBaseMapView.setVisibility(View.GONE);
            if (mHandler != null) {
                mHandler.stop();
            }
        } else {
            mBaseMapView.setVisibility(View.VISIBLE);
            if (mHandler != null) {
                mHandler.start();
            }
        }
    }

    public void onStop() {
        mActivity.unregisterReceiver(routeUpdateReceiver);
    }

    public Long getSynchronizeTimestamp() {
        return synchronizeTimestamp;
    }

    public void updateRoute(String route) {
    }

    private void showDialog(final Job job, boolean canBeStarted, boolean canBeCancelled, final AbstractStop stop) {
        String typeName = stop.isPickup() ? mActivity.getString(R.string.collection) : mActivity.getString(R.string.delivery);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_map_marker, null);
        TextView address = (TextView) view.findViewById(R.id.address);
        TextView customer = (TextView) view.findViewById(R.id.customer);
        TextView contactPerson = (TextView) view.findViewById(R.id.contact_person);
        TextView contactPhone = (TextView) view.findViewById(R.id.contact_phone);
        TextView order = (TextView) view.findViewById(R.id.order);
        TextView time = (TextView) view.findViewById(R.id.time);
        time.setText(DateUtils.toStringTime(stop.getDate()) + " (" + stop.getTimeWindowAsString() + ")");
        order.setText(Html.fromHtml("<u>" + stop.getStopName() + " " + typeName + "</u>"));
        order.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (mDialog != null) {
                    mDialog.hide();
                }
                JobWorkflowUtils.openNextActivity(stop, job, mActivity);
            }
        });
        if (StringUtils.isBlank(stop.getAddressAsString())) {
            address.setVisibility(View.GONE);
        } else {
            address.setText(stop.getAddressAsString());
        }
        if (StringUtils.isBlank(stop.getCustomerInfo())) {
            customer.setVisibility(View.GONE);
        } else {
            customer.setVisibility(View.VISIBLE);
            customer.setText(stop.getCustomerInfo());
        }
        if (StringUtils.isBlank(stop.getContactPerson())) {
            contactPerson.setVisibility(View.GONE);
        } else {
            contactPerson.setText(stop.getContactPerson());
        }
        if (StringUtils.isBlank(stop.getContactPhone())) {
            contactPhone.setVisibility(View.GONE);
        } else {
            PhoneUtils.assignPhone(contactPhone, stop.getContactPhone());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setView(view);
        if (canBeStarted) {
            builder.setPositiveButton(mActivity.getString(R.string.start), new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, final int which) {
                    stop.processSetState(TaskState.STOP_ON_ROUTE);
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(mActivity)
                                    .setMessage(R.string.launch_navi_app)
                                    .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, final int which) {
                                            MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), mActivity);
                                        }
                                    })
                                    .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, final int which) {
                                            goToArriveMapActivity(job.getReferenceId(), stop.getReferenceId());
                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        public void onCancel(DialogInterface dialogInterface) {
                                            goToArriveMapActivity(job.getReferenceId(), stop.getReferenceId());
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            });
        }
        if (canBeCancelled) {
            builder.setNegativeButton(R.string.abort_short, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.startActivity(new Intent(mActivity, AbortActivity.class)
                            .putExtra(IntentAttributes.JOB_ID, job.getReferenceId())
                            .putExtra(IntentAttributes.STOP_ID, stop.getReferenceId()));
                    mActivity.finish();
                }
            });
        }
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    private void goToArriveMapActivity(String jobRef, String stopRef) {
        mActivity.startActivity(new Intent(mActivity, ArriveMapActivity.class)
                .putExtra(IntentAttributes.JOB_ID, jobRef)
                .putExtra(IntentAttributes.STOP_ID, stopRef));
        mActivity.finish();
    }

    public void onDCTap(Job job) {
        new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getString(R.string.pickup_location) + ", " + DateUtils.toStringTime(job.getDate()))
                .setMessage(job.getAddressAsString())
                .show();
    }

    public void onEndTap(Job job) {
        if (job.getEndAddress() != null) {
            new AlertDialog.Builder(mActivity)
                    .setMessage(job.getEndAddress().getFullAddress())
                    .show();
        }
    }

    public void onStartTap(Job job) {
        if (job.getEndAddress() != null) {
            new AlertDialog.Builder(mActivity)
                    .setMessage(job.getStartAddress().getFullAddress())
                    .show();
        }
    }

    public void onJobTap(final AbstractStop stop) {
        final Stop startedStop = getStartedStop(stop);
        if (startedStop == null) {
            showArriveDialog(stop);
            return;
        }
        for (AbstractStop o : startedStop.getParentJob().getStops()) {
            if (o.equals(stop) &&
                    stop.isProcessing() &&
                    !stop.isCompleted()) {
                showArriveDialog(stop);
                return;
            }
        }
        new AlertDialog.Builder(mActivity)
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

    private boolean isSameAddress(List<Address> al1, List<Address> al2) {
        for (Address a1 : al1) {
            boolean contain = false;
            for (Address a2 : al2) {
                if (a1.getLatitude().equals(a2.getLongitude()) && a1.getLongitude().equals(a2.getLongitude())) {
                    contain = true;
                    break;
                }
            }
            if (!contain) return false;
        }
        return true;
    }

    public Bitmap drawBitmap(Integer priority, String time, boolean isPu) {
        Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.stop_icon);
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color;
        switch (priority) {
            case 0: {
                color = Color.GREEN;
                break;
            }
            case 1: {
                color = Color.YELLOW;
                break;
            }
            case 2: {
                color = Color.RED;
                break;
            }
            default:
                color = Color.WHITE;
        }
        paint.setColor(color);
        int textSize = (int) (12 * mActivity.getResources().getDisplayMetrics().density);
        paint.setTextSize(textSize);
        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
        Rect bounds = new Rect();
        paint.getTextBounds("⇧ " + time, 0, time.length() + 2, bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (int) ((bitmap.getHeight() - bounds.height()) / 2 + textSize / 1.5);
        canvas.drawText(isPu ? "⇧" : "⇩", x, y, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(time, x + textSize, y, paint);
        return bitmap;
    }

    public void sendUpdateRequest(List<Address> addressList) {
        if (!addressList.isEmpty()) {
            if (addresses.size() == addressList.size() && isSameAddress(addressList, addresses)) {
                return;
            } else {
                addresses.clear();
                addresses.addAll(addressList);
            }
            Bundle bundle = new Bundle(3);
            bundle.putParcelableArrayList(Constants.ADDRESS_LIST, addresses);
            bundle.putLong(Constants.SYNCHRONIZE_TIMESTAMP, getSynchronizeTimestamp());
            ServiceHolder.getInstance().startService(HttpService.class, bundle, Pair.create(IntentAttributes.HTTP_TYPE, Constants.ROUTE_TYPE));
        }
    }

    public void onClick(View v) {
        if (v == mBtnZoomOut) {
            zoomOut();
        } else if (v == mBtnZoomIn) {
            zoomIn();
        } else if (v == mBtnMyLocation) {
            myLocation();
        }
    }

    protected void zoomIn() {
    }

    protected void zoomOut() {
    }

    public void onViewPress() {
        mTrackCurrentPosition = false;
        Log.d("----", "mTrackCurrentPosition = false");
    }

    protected void myLocation() {
        mTrackCurrentPosition = true;
        Log.d("----", "mTrackCurrentPosition = true");
    }

    private Stop getStartedStop(AbstractStop stop) {
        for (Object o : stop.getParentJob().getStops()) {
            Stop s = (Stop) o;
            if (s.isProcessing() && !s.isCompleted()) {
                return s;
            }
        }
        return null;
    }

    private void processStop(final AbstractStop stop) {
        stop.processSetState(TaskState.STOP_ON_ROUTE);
        new AlertDialog.Builder(mActivity)
                .setMessage(R.string.launch_navi_app)
                .setPositiveButton(R.string.mx_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MxAndroidUtil.showTomTomOrDefaultNavigator(stop.getAddress(), mActivity);
                        dialog.dismiss();
                        mActivity.finish();
                    }
                })
                .setNegativeButton(R.string.mx_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        goToArriveMapActivity(stop.getParentJob().getReferenceId(), stop.getReferenceId());
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showArriveDialog(final AbstractStop stop) {
        int state = stop.getState();
        showDialog((Job) stop.getParentJob(),
                !(TaskState.STOP_IDLE != state && TaskState.STOP_RUN_ACCEPTED != state && TaskState.STOP_RUN_STARTED != state),
                !stop.isCompleted(),
                stop);
    }
}