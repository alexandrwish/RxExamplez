package com.magenta.mc.client.android.setup;

import android.content.Context;

import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.SmokeApplicationIcons;

public class SmokeUI extends AndroidUI {

    public SmokeUI(final Context applicationContext) {
        super(applicationContext);
    }

    protected void initNotificationIcons() {
        applicationIcons = new SmokeApplicationIcons();
    }
}