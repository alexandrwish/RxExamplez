package com.magenta.mc.client.android.setup;

import android.content.Context;

import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.storage.Storage;
import com.magenta.mc.client.android.mc.storage.file.FileStorage;
import com.magenta.mc.client.android.mc.ui.UI;
import com.magenta.mc.client.android.mc.update.UpdateCheck;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.update.AndroidUpdateCheck;

public class AndroidSetup extends Setup {

    protected Storage storage;
    protected AndroidUI ui;
    protected UpdateCheck updateCheck;

    public AndroidSetup(final Context applicationContext) {
        initSettings(applicationContext);
        initStorage(applicationContext);
        initUI(applicationContext);
        initUpdateCheck(applicationContext);
    }

    protected void initUpdateCheck(Context applicationContext) {
        updateCheck = new AndroidUpdateCheck(applicationContext);
    }

    protected void initUI(Context applicationContext) {
        ui = new AndroidUI(applicationContext);
    }

    private void initSettings(Context applicationContext) {
        // FIXME: 3/12/17 pls
    }

    protected void initStorage(Context applicationContext) {
        storage = new FileStorage(applicationContext.getDir("storage", Context.MODE_PRIVATE));
    }

    public Storage getStorage() {
        return storage;
    }

    public UI getUI() {
        return ui;
    }

    public UpdateCheck getUpdateCheck() {
        return updateCheck;
    }
}