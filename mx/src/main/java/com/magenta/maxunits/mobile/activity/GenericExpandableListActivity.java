package com.magenta.maxunits.mobile.activity;

import android.app.ExpandableListActivity;
import android.os.Bundle;

import com.magenta.maxunits.mobile.service.listeners.BroadcastEventsListener;

/**
 * @author Sergey Grachev
 */
public abstract class GenericExpandableListActivity<D extends ActivityDecorator> extends ExpandableListActivity {
    protected D decorator;
    protected BroadcastEventsListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorator.onCreate();
    }

    @Override
    protected void onPause() {
        decorator.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        decorator.onResume();
    }
}
