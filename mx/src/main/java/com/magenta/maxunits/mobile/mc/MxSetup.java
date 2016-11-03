package com.magenta.maxunits.mobile.mc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.magenta.maxunits.mobile.events.InstallUpdateEvent;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.mc.client.android.smoke.setup.SmokeSetup;
import com.magenta.mc.client.android.update.AndroidUpdateCheck;
import com.magenta.mc.client.demo.DemoStorageInitializer;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xmpp.extensions.rpc.DefaultRpcResponseHandler;

public class MxSetup extends SmokeSetup {

    private static final String PLATFORM = "ANDROID";
    private final DemoStorageInitializer demoStorageInitializer;
    private Settings settings;

    public MxSetup(final Context applicationContext, final DemoStorageInitializer demoStorageInitializer) {
        super(applicationContext);
        this.demoStorageInitializer = demoStorageInitializer;
        settings = new MxSettings(applicationContext);
    }

    public Settings getSettings() {
        return settings;
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
                updateIntent.setDataAndType(Uri.fromFile(getContext().getFileStreamPath(Setup.get().getSettings().getUpdateApplicationName() + Setup.get().getSettings().getAppVersion() + ".apk")), "application/vnd.android.package-archive");
                return updateIntent;
            }

            @Override
            protected void onUpdateDownloaded() {
                if (checkDownloadedUpdate()) {
                    ServicesRegistry.getCoreService().notifyListeners(new InstallUpdateEvent("INSTALL_UPDATE", getUpdateFile().toString()));
                } else {
                    MxSettings.getInstance().setUpdateDelayed(false);
                }
            }

            public void check() {
                DefaultRpcResponseHandler.isUpdateAvailable(
                        Setup.get().getSettings().getAppVersion(),
                        PLATFORM,
                        Setup.get().getSettings().getUpdateApplicationName());
            }

            @Override
            public void complete(Boolean available) {
                if (!available) {
                    MxSettings.getInstance().setUpdateDelayed(false);
                }
                super.complete(available);
            }
        };
    }
}