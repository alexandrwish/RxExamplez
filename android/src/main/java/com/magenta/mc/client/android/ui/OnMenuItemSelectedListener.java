package com.magenta.mc.client.android.ui;

import android.app.Activity;
import android.view.MenuItem;

/**
 * @autor Petr Popov
 * Created 24.05.12 13:50
 */
public interface OnMenuItemSelectedListener {

    void setActivity(Activity activity);

    boolean onOptionsItemSelected(MenuItem item);

    boolean onContextItemSelected(MenuItem menuItem);
}
