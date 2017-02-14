package com.magenta.mc.client.android.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

public class MxUI extends AndroidUI {

    public MxUI(final Context applicationContext) {
        super(applicationContext);
        notifications = new MxNotifications(applicationContext);
    }

    public static void startActivity(Context from, Class<? extends Activity> to, int flags) {
        try {
            Intent intent = new Intent(from, to);
            intent.setFlags(flags);
            MCLoggerFactory.getLogger(MxUI.class).info("Start activity [" + to.getSimpleName() + "] on [" + from.getClass().getSimpleName() + "] with flags (" + flags + ")");
            from.startActivity(intent);
        } catch (Exception e) {
            MCLoggerFactory.getLogger(MxUI.class).error("Stub!!", e);
        }
    }

    public static void startActivity(Context from, Class<? extends Activity> to) {
        startActivity(from, to, 0);
    }

    public void switchToActivity(Activity currentActivity) {
        super.switchToActivity(currentActivity);
        switch (currentActivity.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_LANDSCAPE: {
                MCLoggerFactory.getLogger(getClass()).debug("orientation changed to landscape in: " + currentActivity);
                break;
            }
            case Configuration.ORIENTATION_PORTRAIT: {
                MCLoggerFactory.getLogger(getClass()).debug("orientation changed to portrait in: " + currentActivity);
                break;
            }
            default: {
                MCLoggerFactory.getLogger(getClass()).debug("orientation changed to unknown in: " + currentActivity);
            }
        }
    }
}