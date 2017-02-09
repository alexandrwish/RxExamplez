package com.magenta.mc.client.android.ui.activity;

import android.os.Bundle;

public interface SmokeActivityInterface {

    boolean isHasTitleBar();

    /**
     * @return null if not has menu or resource id
     */
    Integer getTitleBarLeftMenu();

    /**
     * @return null if not has menu or resource id
     */
    Integer getTitleBarRightMenu();

    /**
     * @return null if not has menu or resource id
     */
    Integer getMenu();

    /**
     * @return null if not has customTitle
     */
    String getCustomTitle();

    void initActivity(Bundle savedInstanceState);
}