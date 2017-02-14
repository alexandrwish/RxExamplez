package com.magenta.mc.client.android.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.db.dao.DistributionDAO;
import com.magenta.mc.client.android.entity.Address;
import com.magenta.mc.client.android.entity.MapSettingsEntity;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.DataControllerImpl;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.ui.controller.EmptyMapController;
import com.magenta.mc.client.android.ui.controller.GoogleMapController;
import com.magenta.mc.client.android.ui.controller.MapController;
import com.magenta.mc.client.android.ui.controller.MapletController;
import com.magenta.mc.client.android.ui.controller.YandexMapController;
import com.magenta.mc.client.android.ui.view.Maplet;
import com.magenta.mc.client.android.util.IntentAttributes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends DistributionActivity implements WorkflowActivity {

    MapController mapController;
    boolean trackingEnabled;
    boolean trackingEnabledNewValue;
    Double lat;
    Double lon;

    public String getCustomTitle() {
        return getString(R.string.route);
    }

    public void initActivity(Bundle savedInstanceState) {
        super.initActivity(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        Job j = (Job) ServicesRegistry.getDataController().findJob(currentJobId);
        if (j == null) {
            j = ((DataControllerImpl) ServicesRegistry.getDataController()).findFromHistoryJob(currentJobId);
            if (j == null) {

                finish();
                return;
            }
        }
        Intent intent = getIntent();
        lat = intent.getDoubleExtra(IntentAttributes.LAT, 0d);
        lon = intent.getDoubleExtra(IntentAttributes.LON, 0d);
        List<Stop> tmp = new ArrayList<>(1);
        if (lat != 0 && lon != 0) {
            Stop s = (Stop) j.getStops().get(0);
            Address address = new Address();
            address.setLatitude(lat);
            address.setLongitude(lon);
            Stop stop = new Stop();
            stop.setAddress(address);
            stop.setState(-1);
            stop.setParentJob(s.getParentJob());
            tmp.add(stop);
        }
        List<MapSettingsEntity> entities;
        try {
            entities = DistributionDAO.getInstance(this).getMapSettings(Setup.get().getSettings().getLogin());
        } catch (SQLException ignore) {
            entities = new ArrayList<>(0);
        }
        if (!entities.isEmpty()) {
            switch (entities.get(0).getMapProviderType()) {
                case YANDEX: {
                    mapController = new YandexMapController(this, tmp.isEmpty() ? j.getStops() : tmp, !tmp.isEmpty());
                    break;
                }
                case GOOGLE: {
                    mapController = new GoogleMapController(this, tmp.isEmpty() ? j.getStops() : tmp, !tmp.isEmpty());
                    break;
                }
                default:
                    mapController = new MapletController(this, tmp.isEmpty() ? j.getStops() : tmp, !tmp.isEmpty());
            }
        } else {
            mapController = new EmptyMapController(this, tmp.isEmpty() ? j.getStops() : tmp, !tmp.isEmpty());
        }
        trackingEnabled = MxSettings.getInstance().isMapTrackingEnabled();
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

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mapController.onViewPress();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}