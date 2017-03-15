package com.magenta.mc.client.android.ui.delegate;

import android.view.MenuItem;

import com.magenta.mc.client.android.ui.OnMenuItemSelectedListener;

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

    void setDriverStatus(Object o);
}