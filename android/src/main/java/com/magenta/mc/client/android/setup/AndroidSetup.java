package com.magenta.mc.client.android.setup;

import android.content.Context;

import com.magenta.mc.client.android.settings.AndroidSettings;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.update.AndroidUpdateCheck;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.storage.Storage;
import com.magenta.mc.client.storage.file.FileStorage;
import com.magenta.mc.client.ui.UI;
import com.magenta.mc.client.update.UpdateCheck;
import com.magenta.mc.client.util.PlatformUtil;

public class AndroidSetup extends Setup {

    protected Storage storage;
    protected Settings settings;
    protected AndroidUtil androidUtil;
    protected AndroidUI ui;
    protected UpdateCheck updateCheck;

    public AndroidSetup(final Context applicationContext) {
        initSettings(applicationContext);
        initStorage(applicationContext);
        initPlatformUtil(applicationContext);
        initUI(applicationContext);
        initUpdateCheck(applicationContext);
    }

    protected void initUpdateCheck(Context applicationContext) {
        updateCheck = new AndroidUpdateCheck(applicationContext);
    }

    protected void initUI(Context applicationContext) {
        ui = new AndroidUI(applicationContext);
    }

    protected void initPlatformUtil(Context applicationContext) {
        androidUtil = new AndroidUtil(applicationContext);
    }

    private void initSettings(Context applicationContext) {
        settings = new AndroidSettings(applicationContext);
    }

    protected void initStorage(Context applicationContext) {
        storage = new FileStorage(applicationContext.getDir("storage", Context.MODE_PRIVATE));
    }

    public Storage getStorage() {
        return storage;
    }

    public Settings getSettings() {
        return settings;
    }

    public PlatformUtil getPlatformUtil() {
        return androidUtil;
    }

    public UI getUI() {
        return ui;
    }

    public UpdateCheck getUpdateCheck() {
        return updateCheck;
    }
}