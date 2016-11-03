package com.magenta.mc.client.setup;

import com.magenta.mc.client.demo.DemoStorageInitializer;
import com.magenta.mc.client.demo.SnapshotInitializer;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.storage.Storage;
import com.magenta.mc.client.ui.TaskUpdateController;
import com.magenta.mc.client.ui.UI;
import com.magenta.mc.client.update.UpdateCheck;
import com.magenta.mc.client.util.PlatformUtil;
import com.magenta.mc.client.util.PlatfromUtilDefault;

/**
 * @author Petr Popov
 *         Created: 13.12.11 17:12
 */
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
        instance.afterSet();
    }

    public Storage getStorage() {
        throw new UnsupportedOperationException("getStorage is not supported in base class, please override it");
    }

    public Settings getSettings() {
        throw new UnsupportedOperationException("getSettings is not supported in base class, please override it");
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

    public TaskUpdateController getTaskUpdateController() {
        throw new UnsupportedOperationException("getTaskUpdateController is not supported in base class, please override it");
    }

    public DemoStorageInitializer getStorageInitializer() {
        return new SnapshotInitializer();
    }

    public void afterSet() {
        //place your code here to exec after setup set
    }

}

