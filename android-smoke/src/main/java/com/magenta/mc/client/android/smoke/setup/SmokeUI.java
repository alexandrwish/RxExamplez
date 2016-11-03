package com.magenta.mc.client.android.smoke.setup;

import android.content.Context;

import com.magenta.mc.client.android.smoke.ui.SmokeApplicationIcons;
import com.magenta.mc.client.android.ui.AndroidUI;

/**
 * @autor Petr Popov
 * Created 22.06.12 12:58
 */
public class SmokeUI extends AndroidUI {

    public SmokeUI(final Context applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void initNotificationIcons() {
        applicationIcons = new SmokeApplicationIcons();
    }

}
