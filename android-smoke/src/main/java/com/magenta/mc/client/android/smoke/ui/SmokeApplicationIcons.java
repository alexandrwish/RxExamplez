package com.magenta.mc.client.android.smoke.ui;

import com.magenta.mc.client.android.smoke.R;
import com.magenta.mc.client.android.ui.ApplicationIcons;

/**
 * @author Sergey Grachev
 */
public class SmokeApplicationIcons implements ApplicationIcons {
    @Override
    public int getApplication() {
        return R.drawable.mc_img_application;
    }

    @Override
    public int getOnline() {
        return R.drawable.mc_img_notification_online;
    }

    @Override
    public int getOffline() {
        return R.drawable.mc_img_notification_offline;
    }

    @Override
    public int getAlert() {
        return R.drawable.mc_img_notification_alert;
    }
}
