package com.magenta.mc.client.android.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.magenta.mc.client.android.ui.theme.Theme;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;

import java.io.File;

/**
 * @author Petr Popov
 *         Created: 26.12.11 20:51
 */
public class AndroidSettings extends Settings {
    public static final String PROPERTY_UPDATE_SERVER_PORT = "update.server.port";
    public static final String PROPERTY_UI_THEME = "ui.theme";

    public static final int PROPERTY_UPDATE_SERVER_PORT_DEFAULT = 8280;
    public static final Theme PROPERTY_UI_THEME_DEFAULT = Theme.night;

    protected Context applicationContext;

    public AndroidSettings(Context applicationContext) {
        super(applicationContext);
        setInstance(this);
    }

    @Override
    public File getLogFolder() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return new File(Environment.getExternalStorageDirectory(), this.getAppName());
        }
        return super.getLogFolder();
    }

    @Override
    protected void preInit(Object context) {
        this.applicationContext = (Context) context;
        setAppFolder(applicationContext.getDir("settings", Context.MODE_PRIVATE));
    }

    @Override
    protected void initAppNameAndVersion() {
        PackageManager packageManager = applicationContext.getPackageManager();
        appName = packageManager.getApplicationLabel(applicationContext.getApplicationInfo()).toString();
        try {
            appVersion = packageManager.getPackageInfo(applicationContext.getPackageName(), PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int getUpdateServerPort() {
        try {
            return Integer.valueOf(getProperty(PROPERTY_UPDATE_SERVER_PORT));
        } catch (Exception ignore) {
            return PROPERTY_UPDATE_SERVER_PORT_DEFAULT;
        }
    }

    public void setUpdateServerPort(final int updateServerPort) {
        setProperty(PROPERTY_UPDATE_SERVER_PORT, Integer.toString(updateServerPort));
    }

    public Theme getApplicationTheme() {
        String propertyVal = getProperty(PROPERTY_UI_THEME);
        Theme val = null;
        if (propertyVal != null && !propertyVal.trim().isEmpty()) {
            try {
                int code = Integer.parseInt(propertyVal);
                val = Theme.lookup(code);
            } catch (NumberFormatException nfe) {
                MCLoggerFactory.getLogger().warn(nfe);
            }
        }
        if (val == null) {
            val = PROPERTY_UI_THEME_DEFAULT;
        }
        return val;
    }

    public void savePropertyUiTheme(Theme theme) {
        setProperty(PROPERTY_UI_THEME, String.valueOf(theme.getCode()));

        saveSettings();
    }
}
