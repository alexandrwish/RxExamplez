package com.magenta.mc.client.android.smoke.activity;

import android.os.Bundle;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 27.12.13 12:10
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
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
