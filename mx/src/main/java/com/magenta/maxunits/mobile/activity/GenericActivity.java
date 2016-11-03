package com.magenta.maxunits.mobile.activity;

import android.os.Bundle;

import com.magenta.mc.client.android.smoke.activity.SmokeActivity;

/**
 * @author Sergey Grachev
 */
public abstract class GenericActivity<D extends ActivityDecorator> extends SmokeActivity {

    protected D decorator;

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