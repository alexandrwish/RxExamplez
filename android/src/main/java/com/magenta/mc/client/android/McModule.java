package com.magenta.mc.client.android;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.magenta.mc.client.android.ui.delegate.ActivityDelegate;
import com.magenta.mc.client.android.ui.delegate.McActivityDelegate;

public class McModule implements Module {

    public void configure(Binder binder) {
        binder.bind(ActivityDelegate.class).to(McActivityDelegate.class);
    }
}