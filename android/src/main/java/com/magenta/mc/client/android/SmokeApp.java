package com.magenta.mc.client.android;

import android.content.Context;

import com.magenta.mc.client.android.setup.SmokeSetup;
import com.magenta.mc.client.setup.Setup;

public class SmokeApp extends AndroidApp {

    public SmokeApp(String[] args, Context applicationContext) {
        super(args, applicationContext);
    }

    protected void initSetup() {
        super.initSetup();
        Setup.init(new SmokeSetup(applicationContext));
    }
}