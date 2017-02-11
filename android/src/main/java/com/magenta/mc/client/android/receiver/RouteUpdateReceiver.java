package com.magenta.mc.client.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.ui.controller.MapController;

public class RouteUpdateReceiver extends BroadcastReceiver {

    private final MapController mapController;

    public RouteUpdateReceiver(MapController mapController) {
        this.mapController = mapController;
    }

    public void onReceive(Context context, Intent intent) {
        if (mapController.getSynchronizeTimestamp().equals(intent.getLongExtra("synchronizeTimestamp", -1L))) {
            mapController.updateRoute(intent.getStringExtra("route"));
        }
    }
}