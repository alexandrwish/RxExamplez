package com.magenta.mc.client.android.ui.activity;

import android.view.MenuItem;

import com.magenta.mc.client.android.ui.delegate.ActivityDelegate;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;

public class GenericActivity<D extends ActivityDelegate> extends RoboActivity implements IGenericActivity<D> {

    @Inject
    private D delegate;

    protected void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    protected void onResume() {
        super.onResume();
        getDelegate().onResume();
    }

    protected void onPause() {
        super.onPause();
        getDelegate().onPause();
    }

    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
        getDelegate().onBackPressed();
    }

    public boolean onContextItemSelected(MenuItem item) {
        return getDelegate().onContextItemSelected(item);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return getDelegate().onOptionsItemSelected(item);
    }

    public D getDelegate() {
        return delegate;
    }
}