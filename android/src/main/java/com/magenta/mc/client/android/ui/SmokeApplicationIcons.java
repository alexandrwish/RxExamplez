package com.magenta.mc.client.android.ui;

import com.magenta.mc.client.android.R;

public class SmokeApplicationIcons implements ApplicationIcons {

    public int getApplication() {
        return R.drawable.mc_img_application;
    }

    public int getOnline() {
        return R.drawable.mc_img_notification_online;
    }

    public int getOffline() {
        return R.drawable.mc_img_notification_offline;
    }

    public int getAlert() {
        return R.drawable.mc_img_notification_alert;
    }
}