package com.magenta.mc.client.android.setup;

import android.content.Context;

public class SmokeSetup extends AndroidSetup {

    public SmokeSetup(final Context applicationContext) {
        super(applicationContext);
    }

    protected void initUI(Context applicationContext) {
        ui = new SmokeUI(applicationContext);
    }
}