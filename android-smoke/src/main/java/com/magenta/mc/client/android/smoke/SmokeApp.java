package com.magenta.mc.client.android.smoke;

import android.content.Context;

import com.magenta.mc.client.android.AndroidApp;
import com.magenta.mc.client.android.smoke.setup.SmokeSetup;
import com.magenta.mc.client.setup.Setup;

/**
 * @autor Petr Popov
 * Created 22.06.12 13:00
 */
public class SmokeApp extends AndroidApp {

    public SmokeApp(String[] args, Context applicationContext) {
        super(args, applicationContext);
    }

    @Override
    protected void initSetup() {
        super.initSetup();
        Setup.init(new SmokeSetup(applicationContext));
    }

}
