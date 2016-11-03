package com.magenta.mc.client.android.ui.activity;

import android.view.MenuItem;

import com.google.inject.Inject;
import com.magenta.mc.client.android.ui.OnMenuItemSelectedListener;
import com.magenta.mc.client.android.ui.delegate.ActivityDelegate;

import roboguice.activity.RoboActivity;

/**
 * @autor Sergey Grachev
 * @autor Petr Popov
 * Created 16.04.12 18:29
 */
public abstract class GenericActivity extends RoboActivity {

    @Inject
    private ActivityDelegate delegate;

//    private DialogFragmentManager dialogFragmentManager;

    @Override
    protected void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDelegate().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getDelegate().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getDelegate().onBackPressed();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return getDelegate().onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getDelegate().onOptionsItemSelected(item);
    }

    public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener listener) {
        getDelegate().setOnMenuItemSelectedListener(listener);
    }

    public ActivityDelegate getDelegate() {
        return delegate;
    }

//    public DialogFragmentManager getDialogFragmentManager() {
//        if (dialogFragmentManager == null) {
//            dialogFragmentManager = new DialogFragmentManager(this);
//        }
//        return dialogFragmentManager;
//    }
}