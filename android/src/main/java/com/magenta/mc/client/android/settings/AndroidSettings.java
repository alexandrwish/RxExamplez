package com.magenta.mc.client.android.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.magenta.mc.client.android.ui.theme.Theme;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.PropertyEventListener;
import com.magenta.mc.client.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class AndroidSettings extends Settings {

    public static final String PROPERTY_UI_THEME = "ui.theme";
    private static final String PROPERTY_UPDATE_SERVER_PORT = "update.server.port";
    private static final int PROPERTY_UPDATE_SERVER_PORT_DEFAULT = 8280;
    private static final Theme PROPERTY_UI_THEME_DEFAULT = Theme.night;

    protected Context applicationContext;

    public AndroidSettings(Context applicationContext) {
        super(applicationContext);
        setInstance(this);
    }

    public File getLogFolder() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return new File(Environment.getExternalStorageDirectory(), this.getAppName());
        }
        return super.getLogFolder();
    }

    protected void preInit(Object context) {
        this.applicationContext = (Context) context;
        setAppFolder(applicationContext.getDir("settings", Context.MODE_PRIVATE));
    }

    protected void init(Object context) {
        try {
            initDefaultValues();
            initAppNameAndVersion(); //load appName & and versions from jar, and then from file if exists
            getApplicationFolder(); // init appFolder property
            // load settings from jar first, overwriting default values
            inboundProperties.load(new InputStreamReader(((Context) context).getResources().getAssets().open("settings.properties")));
            putAll(inboundProperties);
            // now try to load properties from file (overriden by user or updater)
            loadSettingsFromFile();
            saveSettings(); // at this time settings may not exist, if loaded from jar
            initHosts();
            addPropertyListener(new PropertyEventListener() {
                public void propertyChanged(String property, String oldValue, String newValue) {
                    if (Settings.HOST.equals(property)) {
                        initHosts();
                    }
                }
            });
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).debug("Settings loading failed: " + e.getMessage());
        }
    }

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
}