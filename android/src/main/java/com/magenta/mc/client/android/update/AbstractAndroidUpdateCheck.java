package com.magenta.mc.client.android.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.update.UpdateCheck;
import com.magenta.mc.client.android.mc.util.FileUtils;
import com.magenta.mc.client.android.mc.xmpp.extensions.rpc.DefaultRpcResponseHandler;
import com.magenta.mc.client.android.settings.AndroidSettings;
import com.magenta.mc.client.android.ui.AndroidUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

abstract class AbstractAndroidUpdateCheck implements UpdateCheck {

    private static final String NOTIFICATION_TAG = AbstractAndroidUpdateCheck.class.getName();
    private static final long MIN_REQUEST_PERIOD = 30 * 60 * 1000;
    private static final String PLATFORM = "ANDROID";
    private static final int NOTIFICATION_ID = 1;
    private static final String APK = ".apk";
    private static final String CRC = ".crc";
    private static long lastRequest = -1;
    private final Context context;

    AbstractAndroidUpdateCheck(final Context context) {
        this.context = context;
    }

    private static void writeFile(final InputStream is, final OutputStream os) throws Throwable {
        int read;
        final byte[] bytes = new byte[1024];
        while ((read = is.read(bytes)) != -1) {
            os.write(bytes, 0, read);
        }
    }

    public void check() {
        if (System.currentTimeMillis() - lastRequest < MIN_REQUEST_PERIOD) {
            // nothing, imei check has already been done by this user since application start
            MCLoggerFactory.getLogger(AndroidUpdateCheck.class).debug("skipping update check as it was done recently");
        } else {
            DefaultRpcResponseHandler.isUpdateAvailable(
                    Setup.get().getSettings().getAppVersion(),
                    PLATFORM,
                    Setup.get().getSettings().getUpdateApplicationName());
        }
    }

    public void updateReported(final String platform, final String application) {
        final String thisApplication = Setup.get().getSettings().getUpdateApplicationName();
        if (!PLATFORM.equalsIgnoreCase(platform) || !thisApplication.equalsIgnoreCase(application)) {
            return;
        }
        MobileApp.runTask(new Runnable() {
            public void run() {
                DefaultRpcResponseHandler.isUpdateAvailable(Setup.get().getSettings().getAppVersion(), PLATFORM, thisApplication);
            }
        });
    }

    public void complete(final Boolean available) {
        MCLoggerFactory.getLogger(AndroidUpdateCheck.class).debug("UC: UpdateCheckImpl done");
        lastRequest = System.currentTimeMillis();
        // run asynchronously not to block the communication thread
        MobileApp.runTask(new Runnable() {
            public void run() {
                if (Boolean.TRUE.equals(available)) {
                    MCLoggerFactory.getLogger(getClass()).debug("UC: Update available, download APK");
                    try {
                        if (downloadUpdate()) {
                            onUpdateDownloaded();
                        }
                    } catch (Throwable throwable) {
                        MCLoggerFactory.getLogger(getClass()).error("UC: Error: Failed download APK");
                        throwable.printStackTrace();
                    }
                } else {
                    MCLoggerFactory.getLogger(getClass()).debug("UC: No update available");
                }
            }
        });
    }

    public boolean checkDownloadedUpdate() {
        if (isDownloadedUpdateAvailable()) {
            return true;
        } else {
            cleanUpdateDir(false);
            return false;
        }
    }

    protected synchronized boolean downloadUpdate() throws Throwable {
        boolean success = false;
        final String currentVersionUrl = getUpdateServiceUrl();
        final HttpURLConnection conn = (HttpURLConnection) new URL(currentVersionUrl).openConnection();
        try {
            final InputStream is = conn.getInputStream();
            try {
                if ((conn.getContentType() != null) && (conn.getContentType().equals("application/vnd.android.package-archive"))) {
                    File apkFile = getUpdateFile();
                    File crcFile = getCrcFile();
                    if (apkFile.exists() && crcFile.exists()) {
                        // already downloaded, ignore
                        return true;
                    } else {
                        if (apkFile.exists()) {
                            FileUtils.deleteFile(apkFile);
                        }
                        if (crcFile.exists()) {
                            FileUtils.deleteFile(crcFile);
                        }
                        FileOutputStream os = getContext().openFileOutput(getUpdateFile().getName(), Context.MODE_WORLD_READABLE);
                        try {
                            writeFile(is, os);
                            success = true;
                        } finally {
                            os.close();
                        }
                        // now we create 'crc' file to confirm successful download
                        createCrcFile();
                    }
                }
            } finally {
                is.close();
            }
        } finally {
            conn.disconnect();
        }
        return success;
    }

    void createCrcFile() throws IOException {
        String crc32 = Long.toHexString(org.apache.commons.io.FileUtils.checksumCRC32(getUpdateFile()));
        org.apache.commons.io.FileUtils.writeStringToFile(getCrcFile(), crc32);
    }

    protected String getCrc() throws IOException {
        File crcFile = getCrcFile();
        if (crcFile != null && crcFile.exists()) {
            return org.apache.commons.io.FileUtils.readFileToString(crcFile);
        }
        return "0";
    }

    protected String getUpdateServiceUrl() {
        final String applicationName = Setup.get().getSettings().getUpdateApplicationName();
        return getUpdateServerUrl()
                + "/" + Setup.get().getSettings().getServerComponentName()
                + "/" + PLATFORM
                + "/" + Setup.get().getSettings().getAppVersion()
                + (applicationName != null && applicationName.length() != 0 ? "/" + applicationName : "");
    }

    protected void onUpdateDownloaded() {
        createNotification();
    }

    protected Context getContext() {
        return context;
    }

    protected Intent createUpdateIntent() {
        final Intent updateIntent = new Intent("android.intent.action.VIEW");
        updateIntent.setDataAndType(Uri.fromFile(getUpdateFile()), "application/vnd.android.package-archive");
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        updateIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return updateIntent;
    }

    private void createNotification() {
        Context context = getContext();
        Intent updateIntent = createUpdateIntent();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(((AndroidUI) Setup.get().getUI()).getApplicationIcons().getAlert())
                .setContentTitle(getContext().getString(R.string.mc_update_notification_title))
                .setContentText(getContext().getString(R.string.mc_update_notification_msg))
                .setContentIntent(PendingIntent.getActivity(getContext(), 0, updateIntent, 0))
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID);
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification);
    }

    protected File getUpdatesFolder() {
        return getContext().getFilesDir();
    }

    protected File getUpdateFile() {
        return new File(getUpdatesFolder(), getUpdateFileName() + APK);
    }

    protected File getCrcFile() {
        return new File(getUpdatesFolder(), getUpdateFileName() + CRC);
    }

    void cleanUpdateDir(boolean excludeDownloadedUpdate) {
        File crcFile = getCrcFile();
        File updateFile = getUpdateFile();
        if (excludeDownloadedUpdate && crcFile != null && updateFile != null) {
            File[] files = getUpdatesFolder().listFiles();
            if (files != null && files.length > 0) {
                for (File nextFile : files) {
                    if (!(nextFile.equals(crcFile) || nextFile.equals(updateFile))) {
                        FileUtils.deleteFile(nextFile);
                    }
                }
            }
        } else {
            FileUtils.deleteDirectory(getUpdatesFolder());
        }
    }

    private boolean isDownloadedUpdateAvailable() {
        File apkFile = getUpdateFile();
        File crcFile = getCrcFile();
        boolean allFilesExist = apkFile.exists() && crcFile.exists();
        if (allFilesExist) {
            try {
                String apkFilePath = apkFile.getPath();
                PackageInfo pi = getContext().getPackageManager().getPackageArchiveInfo(apkFilePath, 0);
                String newVersion = pi.versionName;
                String oldVersion = Setup.get().getSettings().getAppVersion();
                if (!newVersion.equals(oldVersion)) {
                    MCLoggerFactory.getLogger(AndroidUpdateCheck.class).warn("versions is different (new: " + newVersion + ", old:" + oldVersion + ") updating...");
                    return true;
                } else {
                    MCLoggerFactory.getLogger(AndroidUpdateCheck.class).warn("versions is same, do not updating...");
                    return false;
                }
            } catch (Exception e) {
                MCLoggerFactory.getLogger(AndroidUpdateCheck.class).warn("can't parse update apk file version", e);
                return false;
            }
        }
        return false;
    }

    public void installDownloadedUpdate() {
        Intent updateIntent = createUpdateIntent();
        getContext().startActivity(updateIntent);
    }

    String getUpdateFileName() {
        return Setup.get().getSettings().getUpdateApplicationName() + Setup.get().getSettings().getAppVersion();
    }

    protected String getMyUploadApiVersion() {
        return "v2";
    }

    private String getUpdateServerUrl() {
        final AndroidSettings settings = (AndroidSettings) AndroidSettings.get();
        String url = settings.getProperty("update.server.url.pattern", "http://%s:%d/mc/update-servlet/v2");
        String myApiVersion = getMyUploadApiVersion();
        if (url.endsWith("/")) {
            myApiVersion += "/";
        }
        if (!url.endsWith(myApiVersion)) {
            url = url.substring(0, url.length() - myApiVersion.length()) + myApiVersion;
        }
        return String.format(url, settings.getHost(), settings.getUpdateServerPort());
    }
}