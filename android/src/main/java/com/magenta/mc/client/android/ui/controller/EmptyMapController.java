package com.magenta.mc.client.android.ui.controller;

import android.app.Activity;
import android.view.View;

import com.magenta.mc.client.android.entity.AbstractStop;

import java.util.List;

public class EmptyMapController extends MapController {

    public EmptyMapController(Activity activity, List<AbstractStop> stops, boolean routeWithDriver) {
        super(activity, stops, routeWithDriver);
        mBaseMapView.setVisibility(View.GONE);
    }

    public void changeVisibility() {

    }
}