package com.magenta.mc.client.android;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.magenta.mc.client.android.ui.delegate.ActivityDelegate;
import com.magenta.mc.client.android.ui.delegate.McActivityDelegate;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 26.12.13 8:50
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public class McModule implements Module {

    public void configure(Binder binder) {
        binder.bind(ActivityDelegate.class).to(McActivityDelegate.class);
    }
}
