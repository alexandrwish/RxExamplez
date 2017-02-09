package com.magenta.mc.client.android.ui;

import android.app.Activity;
import android.view.MenuItem;

public interface OnMenuItemSelectedListener {

    void setActivity(Activity activity);

    boolean onOptionsItemSelected(MenuItem item);

    boolean onContextItemSelected(MenuItem menuItem);
}