package com.magenta.mc.client.android.smoke;

import com.google.inject.Binder;
import com.magenta.mc.client.android.McModule;
import com.magenta.mc.client.android.smoke.activity.delegate.SmokeActivityDelegate;
import com.magenta.mc.client.android.ui.delegate.ActivityDelegate;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 27.12.13 10:13
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public class SmokeModule extends McModule {

    @Override
    public void configure(Binder binder) {
        binder.bind(ActivityDelegate.class).to(SmokeActivityDelegate.class);
    }
}
