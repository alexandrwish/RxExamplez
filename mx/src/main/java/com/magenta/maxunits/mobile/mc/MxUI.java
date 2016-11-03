package com.magenta.maxunits.mobile.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.smoke.setup.SmokeUI;
import com.magenta.mc.client.log.MCLoggerFactory;

/**
 * Project: Santa-cruz
 * Author:  Alexandr Komarov
 * Created: 17.12.13 10:50
 * <p/>
 * Copyright (c) 1999-2013 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public class MxUI extends SmokeUI {

    public MxUI(final Context applicationContext) {
        super(applicationContext);
        notifications = new MxNotifications(applicationContext);
    }

    public static void startActivity(Context from, Class<? extends Activity> to, int flags) {
        try {
            Intent intent = new Intent(from, to);
            intent.setFlags(flags);

            MCLoggerFactory.getLogger(MxUI.class).info("Start activity [" + to.getSimpleName()
                    + "] on [" + from.getClass().getSimpleName()
                    + "] with flags (" + flags + ")");

            from.startActivity(intent);
        } catch (Exception e) {
            MCLoggerFactory.getLogger(MxUI.class).error("Stub!!", e);
        }
    }

    public static void startActivity(Context from, Class<? extends Activity> to) {
        startActivity(from, to, 0);
    }
}
