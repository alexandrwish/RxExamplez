package com.magenta.mc.client.android.mc;

import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.events.InstallUpdateEvent;
import com.magenta.mc.client.android.mc.demo.DemoStorageInitializer;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.setup.AndroidSetup;
import com.magenta.mc.client.android.update.AndroidUpdateCheck;

public class MxSetup extends AndroidSetup {

    private static final String PLATFORM = "ANDROID";
    private final DemoStorageInitializer demoStorageInitializer;

    public MxSetup(final Context applicationContext, final DemoStorageInitializer demoStorageInitializer) {
        super(applicationContext);
        this.demoStorageInitializer = demoStorageInitializer;
    }

    public DemoStorageInitializer getStorageInitializer() {
        return demoStorageInitializer;
    }

    protected void initUI(final Context applicationContext) {
        ui = new MxUI(applicationContext) {

            protected void initNotifications(final Context context) {
                notifications = new MxNotifications(context);
            }
        };
    }

    protected void initPlatformUtil(final Context applicationContext) {
        androidUtil = new MxAndroidUtil.DeprecatedUtils(applicationContext);
    }

    protected void initStorage(final Context applicationContext) {
        storage = new MxFileStorage(applicationContext.getDir("storage", Context.MODE_PRIVATE));
    }

    protected void initUpdateCheck(final Context applicationContext) {
        updateCheck = new AndroidUpdateCheck(applicationContext) {
            protected Intent createUpdateIntent() {
                final Intent updateIntent = new Intent("android.intent.action.VIEW");
//                updateIntent.setDataAndType(Uri.fromFile(getContext().getFileStreamPath(Setup.get().getSettings().getUpdateApplicationName() + Setup.get().getSettings().getAppVersion() + ".apk")), "application/vnd.android.package-archive");
                return updateIntent;
            }

            protected void onUpdateDownloaded() {
                if (checkDownloadedUpdate()) {
                    ServicesRegistry.getCoreService().notifyListeners(new InstallUpdateEvent("INSTALL_UPDATE", getUpdateFile().toString()));
                } else {
//                    MxSettings.getInstance().setUpdateDelayed(false);
                }
            }

            public void check() {
//                DefaultRpcResponseHandler.isUpdateAvailable(
//                        Setup.get().getSettings().getAppVersion(),
//                        PLATFORM,
//                        Setup.get().getSettings().getUpdateApplicationName());
            }

            public void complete(Boolean available) {
                if (!available) {
//                    MxSettings.getInstance().setUpdateDelayed(false);
                }
                super.complete(available);
            }
        };
    }
}