package com.magenta.maxunits.mobile.dlib.controller;

import android.app.Activity;
import android.view.View;

import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;

import java.util.List;

public class EmptyMapController extends MapController {

    public EmptyMapController(Activity activity, List<Stop> stops, boolean routeWithDriver) {
        super(activity, stops, routeWithDriver);
        mBaseMapView.setVisibility(View.GONE);
    }

    public void changeVisibility() {

    }
}