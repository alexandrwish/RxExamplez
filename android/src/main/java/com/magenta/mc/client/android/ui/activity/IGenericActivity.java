package com.magenta.mc.client.android.ui.activity;

import com.magenta.mc.client.android.ui.delegate.ActivityDelegate;

public interface IGenericActivity<D extends ActivityDelegate> {

    D getDelegate();
}