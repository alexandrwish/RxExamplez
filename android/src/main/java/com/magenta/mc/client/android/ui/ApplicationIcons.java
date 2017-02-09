package com.magenta.mc.client.android.ui;

import android.support.annotation.DrawableRes;

public interface ApplicationIcons {

    @DrawableRes
    int getApplication();

    @DrawableRes
    int getOnline();

    @DrawableRes
    int getOffline();

    @DrawableRes
    int getAlert();
}