package com.magenta.mc.client.android.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

import net.sf.microlog.core.Level;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GingerbreadAndroidUpdateCheck extends AbstractAndroidUpdateCheck {

    private static final String UPDATE_ID_SETTING_NAME = "upd_id_key";

    public GingerbreadAndroidUpdateCheck(final Context context) {
        super(context);
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context ctx, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                long savedDownloadId = Setup.get().getSettings().getLongProperty(UPDATE_ID_SETTING_NAME, -1L);
                if (downloadId != savedDownloadId) {
                    MCLoggerFactory.getLogger(getClass()).log(Level.DEBUG, "Ignore notification, because downloadId = " + downloadId + " saved downloadId = " + savedDownloadId);
                    return;
                }
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cur = dm.query(query);
                if (cur != null && cur.moveToFirst()) {
                    int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                        MCLoggerFactory.getLogger(getClass()).log(Level.DEBUG, "update downloaded successfully");
                        onUpdateDownloaded();
                    } else {
                        MCLoggerFactory.getLogger(getClass()).error("error download update. DownloadManager status is " + cur.getInt(columnIndex));
                    }
                } else {
                    MCLoggerFactory.getLogger(getClass()).log(Level.DEBUG, "download info not found in android db");
                }
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void complete(final Boolean available) {
        if (!available) {
            Setup.get().getSettings().remove(UPDATE_ID_SETTING_NAME);
            return;
        }
        try {
            MCLoggerFactory.getLogger(getClass()).debug("Start downloading update");
            downloadUpdate();
        } catch (Throwable throwable) {
            MCLoggerFactory.getLogger(getClass()).debug("Update downloading error", throwable);
        }
    }

    protected File getUpdatesFolder() {
        String relativePath = "/" + Setup.get().getSettings().getUpdateApplicationName();
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + relativePath).mkdirs();
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + relativePath);
    }

    private int getUpdateStatus() {
        long updaterID = Setup.get().getSettings().getLongProperty(UPDATE_ID_SETTING_NAME, -1L);
        if (updaterID > 0) {
            DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(updaterID);
            Cursor cursor = dm.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        }
        return 0;
    }

    protected boolean downloadUpdate() throws Throwable {
        int updateStatus = getUpdateStatus();
        MCLoggerFactory.getLogger(getClass()).log(Level.DEBUG, "current download status = " + updateStatus);
        if (updateStatus == DownloadManager.STATUS_RUNNING || updateStatus == DownloadManager.STATUS_PAUSED) {
            return false;
        }
        String url = getUpdateServiceUrl();
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("HEAD");
        int responseCode = conn.getResponseCode();
        if (responseCode < 200 || responseCode >= 400) {
            MCLoggerFactory.getLogger(getClass()).log(Level.ERROR, "server response with code" + responseCode);
            return false;
        }
        String localCrc32 = getCrc();
        String updateCrc32 = conn.getHeaderField("crc32");
        if (updateCrc32 != null && localCrc32 != null && !localCrc32.equals("0") && updateCrc32.equals(localCrc32)) {
            MCLoggerFactory.getLogger(getClass()).debug("File \"" + getUpdateFile() + "\" already downloaded");
            return false;
        }
        MCLoggerFactory.getLogger(getClass()).log(Level.DEBUG, "try download update from " + url);
        cleanUpdateDir(false);
        File updateFile = getUpdateFile();
        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        long updaterID = manager.enqueue(new DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Update available")
                .setDescription("Download progress")
                .setVisibleInDownloadsUi(true)
                .setDestinationUri(Uri.fromFile(updateFile)));
        Settings settings = Setup.get().getSettings();
        settings.setProperty(UPDATE_ID_SETTING_NAME, updaterID);
        settings.saveSettings();
        return true;
    }

    protected void onUpdateDownloaded() {
        try {
            createCrcFile();
            super.onUpdateDownloaded();
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).log(Level.ERROR, "can't create crc file " + getCrcFile(), e);
        }
    }

    protected String getMyUploadApiVersion() {
        return "v3";
    }
}