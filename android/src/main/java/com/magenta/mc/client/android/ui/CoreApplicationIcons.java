package com.magenta.mc.client.android.ui;

import com.magenta.mc.client.android.R;

public class CoreApplicationIcons implements ApplicationIcons {

    public int getApplication() {
        return -1;
    }

    public int getOnline() {
        return R.drawable.connect;
    }

    public int getOffline() {
        return R.drawable.disconnect;
    }

    public int getAlert() {
        return -1;
    }
}