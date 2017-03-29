package com.magenta.mc.client.android.ui.delegate;

import android.view.MenuItem;

public interface ActivityDelegate {

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onBackPressed();

    boolean onContextItemSelected(MenuItem item);

    boolean onOptionsItemSelected(MenuItem item);

    void setDriverStatus(Object o);
}