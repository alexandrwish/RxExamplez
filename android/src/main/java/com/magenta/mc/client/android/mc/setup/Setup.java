package com.magenta.mc.client.android.mc.setup;

import com.magenta.mc.client.android.mc.demo.DemoStorageInitializer;
import com.magenta.mc.client.android.mc.demo.SnapshotInitializer;
import com.magenta.mc.client.android.mc.storage.Storage;
import com.magenta.mc.client.android.mc.ui.UI;
import com.magenta.mc.client.android.mc.update.UpdateCheck;
import com.magenta.mc.client.android.mc.util.PlatformUtil;
import com.magenta.mc.client.android.mc.util.PlatfromUtilDefault;

public class Setup {

    private static Setup instance;

    protected Setup() {
    }

    public static Setup get() {
        if (instance == null) {
            throw new RuntimeException("Setup is not initialized");
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static void init(Setup setup) {
        instance = setup;
    }

    public Storage getStorage() {
        throw new UnsupportedOperationException("getStorage is not supported in base class, please override it");
    }

    public PlatformUtil getPlatformUtil() {
        return new PlatfromUtilDefault();
    }

    public UI getUI() {
        throw new UnsupportedOperationException("getStorage is not supported in base class, please override it");
    }

    public UpdateCheck getUpdateCheck() {
        throw new UnsupportedOperationException("getUpdateCheck is not supported in base class, please override it");
    }

    public DemoStorageInitializer getStorageInitializer() {
        return new SnapshotInitializer();
    }
}