package com.magenta.maxunits.mobile.dlib.activity;

import android.os.Bundle;

import com.magenta.mc.client.android.ui.activity.SmokeActivity;

public abstract class GenericActivity<D extends ActivityDecorator> extends SmokeActivity {

    protected D decorator;

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