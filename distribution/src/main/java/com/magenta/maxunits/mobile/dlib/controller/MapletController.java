package com.magenta.maxunits.mobile.dlib.controller;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;
import com.magenta.maxunits.mobile.dlib.view.Maplet;
import com.magenta.maxunits.mobile.entity.Address;

import java.util.ArrayList;
import java.util.List;


public class MapletController extends MapController {

    private final static String TAG = "----MapletController";
    private Maplet maplet;


    public MapletController(Activity activity, List<Stop> stops, boolean routeWithDriver) {
        super(activity, stops, routeWithDriver);
        maplet = new Maplet(activity, this);
        maplet.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mHolder.addView(maplet);
        maplet.setJobs(stops, routeWithDriver);
    }

    public void fitBounds(final List<Address> addresses) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (addresses.isEmpty()) {
                    return;
                }
                List<String> bounds = new ArrayList<String>(addresses.size());
                for (Address address : addresses) {
                    bounds.add("[" + address.getLatitude() + "," + address.getLongitude() + "]");
                }
                String s = "[" + TextUtils.join(",", bounds) + "]";
                maplet.fitBounds(s);

            }
        }, 300);
        Log.d(TAG, "fitBounds " + addresses.size());
    }

    @Override
    public void onMapReady() {
        Log.d(TAG, "onMapReady");
        maplet.showJobs();
        maplet.showCurrentPosition();
        if (!maplet.showStart() || !maplet.showEnd()) {
            maplet.showDC();
        }
        super.onMapReady();
    }

    @Override
    public void updateRoute(String route) {
        maplet.updateRoute(route);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (maplet.isShown()) {
            maplet.resumeRunnable();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (maplet.isShown()) {
            maplet.stopRunnable();
        }
    }

    protected void zoomIn() {
        super.zoomIn();
        maplet.zoomInJs();
    }

    protected void zoomOut() {
        super.zoomOut();
        maplet.zoomOutJs();
    }

    protected void myLocation() {
        super.myLocation();
        maplet.myLocationJs();

    }
}