package com.magenta.mc.client.android;

import android.app.Application;
import android.content.Intent;

import com.google.inject.Module;
import com.magenta.mc.client.android.service.McService;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

import roboguice.RoboGuice;

/**
 * @autor Petr Popov
 * Created 18.04.12 13:08
 */
public abstract class McAndroidApplication extends Application {

    protected static boolean isFirstStart;

    public McAndroidApplication() {
        super();
        MCLoggerFactory.getLogger(getClass()).trace("Instantiating");
    }

    //Override this method for clear Settings when App was started;
    public static void resetSettingsUserId() {
        if (isFirstStart) {
            isFirstStart = false;
            //Clear UserID
            Settings settings = Setup.get().getSettings();
            settings.setUserId("");
            settings.saveSettings();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MCLoggerFactory.getLogger(getClass()).trace("onCreate");
        isFirstStart = true;
        setupGuice();
        startMcService();
    }

    private void setupGuice() {
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(this), createModule());
    }

    protected Module createModule() {
        return new McModule();
    }

    protected void startMcService() {
        MCLoggerFactory.getLogger(getClass()).trace("starting service");
        Intent intent = new Intent(this, getServiceClass());
        intent.putExtra("dont_login", true);
        startService(intent);
    }

    protected abstract Class<? extends McService> getServiceClass();

    @Override
    public void onTerminate() {
        MCLoggerFactory.getLogger(getClass()).trace("onTerminate");
        AndroidApp.getInstance().exit();
        super.onTerminate();
    }
}