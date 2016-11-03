package com.magenta.mc.client.android.ui.activity;

import com.magenta.mc.client.android.ui.delegate.ActivityDelegate;

public interface IGenericActivity<E extends ActivityDelegate> {

    E getDelegate();
}