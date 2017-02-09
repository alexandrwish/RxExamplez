package com.magenta.maxunits.mobile.dlib.activity;

import android.app.ExpandableListActivity;
import android.os.Bundle;

import com.magenta.maxunits.mobile.dlib.service.listeners.BroadcastEventsListener;

public abstract class GenericExpandableListActivity<D extends ActivityDecorator> extends ExpandableListActivity {

    protected D decorator;
    protected BroadcastEventsListener listener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorator.onCreate();
    }

    protected void onPause() {
        decorator.onPause();
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        decorator.onResume();
    }
}