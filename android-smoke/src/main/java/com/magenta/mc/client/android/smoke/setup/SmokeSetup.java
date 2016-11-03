package com.magenta.mc.client.android.smoke.setup;

import android.content.Context;

import com.magenta.mc.client.android.setup.AndroidSetup;

/**
 * @autor Petr Popov
 * Created 22.06.12 12:59
 */
public class SmokeSetup extends AndroidSetup {

    public SmokeSetup(final Context applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void initUI(Context applicationContext) {
        ui = new SmokeUI(applicationContext);
    }

}
