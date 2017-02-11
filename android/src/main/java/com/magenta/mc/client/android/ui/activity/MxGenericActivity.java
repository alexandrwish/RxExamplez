package com.magenta.mc.client.android.ui.activity;

import android.os.Bundle;

import com.magenta.mc.client.android.ui.activity.ActivityDecorator;
import com.magenta.mc.client.android.ui.activity.SmokeActivity;

public abstract class MxGenericActivity<D extends ActivityDecorator> extends SmokeActivity {

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