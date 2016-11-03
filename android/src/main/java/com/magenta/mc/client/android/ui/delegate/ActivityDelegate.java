package com.magenta.mc.client.android.ui.delegate;

import android.view.MenuItem;

import com.magenta.mc.client.android.ui.OnMenuItemSelectedListener;
import com.magenta.mc.client.client.DriverStatus;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 24.12.13 13:38
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public interface ActivityDelegate {

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onBackPressed();

    void setOnMenuItemSelectedListener(OnMenuItemSelectedListener onMenuItemSelectedListener);

    boolean onContextItemSelected(MenuItem item);

    boolean onOptionsItemSelected(MenuItem item);

    void setDriverStatus(DriverStatus driverStatus);
}