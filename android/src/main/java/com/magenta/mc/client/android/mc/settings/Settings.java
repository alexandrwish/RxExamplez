package com.magenta.mc.client.android.mc.settings;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.util.ResourceManager;
import com.magenta.mc.client.android.mc.util.StrUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Deprecated
// TODO: 2/15/17 use shared preferences
public class Settings extends Properties {

    public static final String HOST = "server.address";
    public static final String SERVER_NAME = "server.name";
    public static final String PORT = "server.port";
    public static final String USE_SSL = "useSSL";
    public static final String PLAIN_AUTH = "plainAuth";
    public static final String COMPRESSION = "compression";
    public static final String KEEP_ALIVE_PERIOD = "keepAlive.period";
    public static final String KEEP_ALIVE_TYPE = "keepAlive.type";
    public static final String AUTOLOGIN = "autologin";
    public static final String STORAGE_PATH = "cache.file.path";
    public static final String AUTOAWAY = "autoaway";
    public static final String USER_ID = "user.id";
    public static final String SERVER_COMPONENT_NAME = "server.component.name";
    public static final String XMPP_RESOURCE = "xmpp.resource";
    public static final String TIMEZONE_PROPERTY = "timezone";
    public static final String LOCALE_KEY = "locale.key";
    public static final String LOGGING_ENABLED = "logging.enabled";
    public static final String LOGGING_FILE = "logging.file";
    public static final String LOGGING_CONSOLE = "logging.console";
    public static final String LOG_USING_ONE_LOGGER_PROPERTY = "logging.one.logger";
    public static final String UPDATE_APPLICATION_NAME = "update.applicationName";
    public static final String OFFLINE_VERSION = "offline.version";
    private static final String FRAME_TITLE_SUFFIX = "frame.title.suffix";
    private static final String FILE_NAME = "settings.properties";
    private static final String VERSION_FILE_NAME = "version.properties";
    private static final String DEFAULT_APP_NAME = "mc-client";
    private static final String SERVER_NAME_DEFAULT_VALUE = "mobile-central";
    private static final String SERVER_COMPONENT_NAME_DEFAULT_VALUE = "echo-core";
    private static final String HOST_DEFAULT_VALUE = "127.0.0.1";
    private static final String PORT_DEFAULT_VALUE = "9876";
    private static final String USE_SSL_DEFAULT_FALUE = "false";
    private static final String PLAIN_AUTH_DEFAULT_FALUE = "false";
    private static final String COMPRESSION_DEFAULT_FALUE = "false";
    private static final String KEEP_ALIVE_PERIOD_DEFAULT_FALUE = "300";
    private static final String KEEP_ALIVE_TYPE_DEFAULT_FALUE = "3";
    private static final String XMPP_RESOURCE_DEFAULT_FALUE = "echo-mobile";
    private static final String AUTO_AWAY_DEFAULT_FALUE = "-1";
    private static final String AUTO_LOGIN_DEFAULT_FALUE = "true";
    private static final String STORAGE_PATH_DEFAULT_FALUE = "";
    private static final String LOCALE_KEY_DEFAULT_FALUE = "DEFAULT";
    private static final String FRAME_TITLE_SUFFIX_DEFAULT_FALUE = "";
    private static final String LOGGING_FILE_DEFAULT_VALUE = "false";
    private static final String LOGGING_ENABLED_DEFAULT_VALUE = "false";
    private static final String LOGGING_CONSOLE_DEFAULT_VALUE = "false";
    private static Settings instance;
    private static String DEFAULT_PARENT_FOLDER = "\\Program Files";
    private static File appFolder;
    private static File logFolder;
    protected String appVersion;
    protected String appName;
    protected Properties inboundProperties = new Properties();
    private String password;
    private int devLaunch;
    private long timeDelta;
    private Set propertyListeners = new HashSet();
    private int serverTimezoneOffset;
    private List hosts;
    private String currentHost;

    protected Settings() {
        super();
        init(null);
    }

    protected Settings(Object context) {
        super();
        preInit(context);
        init(context);
    }

    /**
     * Deprecated, use Setup.get().getSettings()
     *
     * @return
     */
    public static Settings get() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    protected static void setInstance(Settings instance) {
        if (Settings.instance != null) {
            MCLoggerFactory.getLogger(Settings.class).debug("Warning: recovering existing Settings instance");
        }
        Settings.instance = instance;
    }

    protected static void setAppFolder(File appFolder) {
        Settings.appFolder = appFolder;
    }

    //add and overwrite settings by overriding this method. dont forget call to super
    protected void initDefaultValues() {
        inboundProperties.put(SERVER_NAME, SERVER_NAME_DEFAULT_VALUE);
        inboundProperties.put(HOST, HOST_DEFAULT_VALUE);
        inboundProperties.put(PORT, PORT_DEFAULT_VALUE);
        inboundProperties.put(USE_SSL, USE_SSL_DEFAULT_FALUE);
        inboundProperties.put(PLAIN_AUTH, PLAIN_AUTH_DEFAULT_FALUE);
        inboundProperties.put(COMPRESSION, COMPRESSION_DEFAULT_FALUE);
        inboundProperties.put(KEEP_ALIVE_PERIOD, KEEP_ALIVE_PERIOD_DEFAULT_FALUE);
        inboundProperties.put(KEEP_ALIVE_TYPE, KEEP_ALIVE_TYPE_DEFAULT_FALUE);
        inboundProperties.put(AUTOLOGIN, AUTO_LOGIN_DEFAULT_FALUE);
        inboundProperties.put(AUTOAWAY, AUTO_AWAY_DEFAULT_FALUE);
        inboundProperties.put(STORAGE_PATH, STORAGE_PATH_DEFAULT_FALUE);
        inboundProperties.put(LOCALE_KEY, LOCALE_KEY_DEFAULT_FALUE);
        inboundProperties.put(FRAME_TITLE_SUFFIX, FRAME_TITLE_SUFFIX_DEFAULT_FALUE);
        inboundProperties.put(LOGGING_ENABLED, LOGGING_ENABLED_DEFAULT_VALUE);
        inboundProperties.put(LOGGING_FILE, LOGGING_FILE_DEFAULT_VALUE);
        inboundProperties.put(LOGGING_CONSOLE, LOGGING_CONSOLE_DEFAULT_VALUE);
    }

    protected void initAppNameAndVersion() {
        // initialize appName and appVersion from version.properties file
        appVersion = "0.1";
        appName = DEFAULT_APP_NAME;
        final Properties versionProp = new Properties();
        try {
            versionProp.load(ResourceManager.getInstance().getResourceAsStream(VERSION_FILE_NAME));
            appVersion = versionProp.getProperty("application.version");
            appName = versionProp.getProperty("application.name");
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).error("cannot load version.properties from jar", e);
        }

        final InputStream stream = openFile(VERSION_FILE_NAME);
        if (stream == null) {
            MCLoggerFactory.getLogger(getClass()).debug("Warning: version.properties file not found");
        } else {
            try {
                versionProp.load(stream);
                appVersion = versionProp.getProperty("application.version");
                appName = versionProp.getProperty("application.name");
                if (appName.indexOf("@") > -1) {
                    // we run from IDE, version.properties contains template, use default
                    appName = DEFAULT_APP_NAME;
                }
            } catch (IOException e) {
                MCLoggerFactory.getLogger(getClass()).debug("Warning: cannot load version.properties");
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isDevLaunch() {
        return devLaunch == 1;
    }

    protected String getParentFolder() {
        if (devLaunch != 2) {
            if (devLaunch == 0) {
                if ("Linux".equals(System.getProperty("os.name"))
                        && !"Dalvik".equals(System.getProperty("java.vm.name"))) {
                    devLaunch = 1;
                    return "mobile_data";
                } else {
                    devLaunch = 2;
                }
            }
        }
        return DEFAULT_PARENT_FOLDER;
    }

    public File getApplicationFolder() {
        if (appFolder == null) {
            return appFolder = new File(getParentFolder() + File.separator + appName + File.separator);
        } else {
            return appFolder;
        }
    }

    public File getLogFolder() {
        return logFolder == null ? (logFolder = getApplicationFolder()) : logFolder;
    }

    public String getResourcesFolder() {
        return new File(getApplicationFolder(), "res").getPath() + File.separator;
    }

    public void addPropertyListener(PropertyEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Property listener is null");
        }
        propertyListeners.add(listener);
    }

    public boolean removePropertyListener(PropertyEventListener listener) {
        return propertyListeners.remove(listener);
    }

    protected void preInit(Object context) {
    }

    protected void init(Object stream) {

    }

    protected void loadSettingsFromFile() throws IOException {
        // Remove old settings
        File dir = getApplicationFolder();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                if (!children[i].equals(getSettingsFileName()) &&
                        children[i].startsWith("settings") && children[i].endsWith(".properties")) {
                    (new File(dir, children[i])).delete();
                }
            }
        }
        InputStream inStream = openFile(getSettingsFileName());
        if (inStream != null) {
            load(inStream);
        }
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppName() {
        return appName;
    }

    protected String getJarSettingsFileName() {
        return FILE_NAME;
    }

    protected String getSettingsFileName() {
        return FILE_NAME;
    }

    /**
     * Returns stream of file in path if it exists or file in jar otherwize
     *
     * @param filename
     * @return
     */
    public InputStream openFile(String filename) {
        final File file = new File(getApplicationFolder(), filename);
        if (file.exists()) { // file not found, let's look inside the jar
            try {
                return file.toURL().openStream();
            } catch (IOException e) {
                MCLoggerFactory.getLogger(getClass()).debug("Can't open file: " + file.getAbsolutePath());
            }
        } else {
            MCLoggerFactory.getLogger(getClass()).debug("Can't find file: " + file.getAbsolutePath());
        }
        return null;
    }

    public void saveSettings() {
        boolean foundPath = false;
        final File folder = getApplicationFolder();
        if (folder.exists() || folder.mkdirs()) {
            foundPath = true;
            OutputStream outStream = null;
            try {
                final File settingsFile = new File(folder, getSettingsFileName());
                if (settingsFile.exists() || settingsFile.createNewFile()) {
                    outStream = new FileOutputStream(settingsFile);
                    this.store(outStream, "echo-mobile settings save");
                    outStream.flush();
                } else {
                    MCLoggerFactory.getLogger(getClass()).debug("Warning: Failed to create settings file: " + settingsFile.getPath());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        //
                    }
                }
            }
        }
        if (!foundPath) {
            MCLoggerFactory.getLogger(getClass()).debug("Warning: Settings path not found, the settings are not saved! path: " + getApplicationFolder());
        }
    }

    public Object setProperty(String key, String newValue) {
        final String oldValue = (String) super.setProperty(key, newValue);
        if (oldValue == null || !oldValue.equalsIgnoreCase(newValue)) {
            triggerPropertyEvent(key, oldValue, newValue);
        }
        return oldValue;
    }

    public Object setProperty(String key, Long newValue) {
        return setProperty(key, String.valueOf(newValue));
    }

    private void triggerPropertyEvent(final String key, final String oldValue, final String newValue) {
        for (Iterator iterator = propertyListeners.iterator(); iterator.hasNext(); ) {
            PropertyEventListener listener = (PropertyEventListener) iterator.next();
            listener.propertyChanged(key, oldValue, newValue);
        }
    }

    public int getIntProperty(String key) {
        return getIntProperty(key, "0");
    }

    public int getIntProperty(String key, String defValue) {
        return Integer.valueOf(getProperty(key, defValue)).intValue();
    }

    public boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, "false");
    }

    public boolean getBooleanProperty(String key, String defValue) {
        return Boolean.valueOf(getProperty(key, defValue).trim()).booleanValue();
    }

    public long getLongProperty(String key, long defValue) {
        String val = getProperty(key);
        if (val != null) {
            try {
                return Long.valueOf(val).longValue();
            } catch (NumberFormatException e) {
            }
        }
        return defValue;
    }

    public String getUserId() {
        return getProperty(USER_ID);
    }

    public void setUserId(String userId) {
        setProperty(USER_ID, userId);
    }

    public String getUserIdAndPassword() {
        return getProperty(USER_ID) + ";" + password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerName() {
        return getProperty(SERVER_NAME, SERVER_NAME_DEFAULT_VALUE);
    }

    public String getServerComponentName() {
        return getProperty(SERVER_COMPONENT_NAME, SERVER_COMPONENT_NAME_DEFAULT_VALUE);
    }

    public String getHost() {
        return getProperty(HOST, HOST_DEFAULT_VALUE);
    }

    public void setHost(String text) {
        setProperty(HOST, text);
        initHosts();
    }

    public int getPort() {
        return getIntProperty(PORT, PORT_DEFAULT_VALUE);
    }

    public void setPort(String text) {
        setProperty(PORT, text);
    }

    public boolean useSSL() {
        return getBooleanProperty(USE_SSL, USE_SSL_DEFAULT_FALUE);
    }

    public boolean getPlainAuth() {
        return getBooleanProperty(PLAIN_AUTH, PLAIN_AUTH_DEFAULT_FALUE);
    }

    public boolean useCompression() {
        return getBooleanProperty(COMPRESSION, COMPRESSION_DEFAULT_FALUE);
    }

    public int keepAlivePeriod() {
        return getIntProperty(KEEP_ALIVE_PERIOD, KEEP_ALIVE_PERIOD_DEFAULT_FALUE);
    }

    public int keepAliveType() {
        return getIntProperty(KEEP_ALIVE_TYPE, KEEP_ALIVE_TYPE_DEFAULT_FALUE);
    }

    public String getLogin() {
        return getUserId();
    }

    public String getJid() {
        return getLogin() + '@' + getServerName() + '/' + getResource();
    }

    public String getBareJid() {
        return getLogin() + '@' + getServerName();
    }

    public String getServerNick() {
        return "server";
    }

    public String getServerJid() {
        return getServerNick() + '@' + getServerName();
    }

    public String getServerComponentJid() {
        return getServerComponentName() + "." + getServerName();
    }

    public String getResource() {
        return getProperty(XMPP_RESOURCE, XMPP_RESOURCE_DEFAULT_FALUE);
    }

    public String getNick() {
        return "driver";
    }

    public int getAutoAwayDelay() {
        return getIntProperty(AUTOAWAY, AUTO_AWAY_DEFAULT_FALUE);
    }

    public boolean isAutoAway() {
        return getAutoAwayDelay() > 0;
    }

    public boolean isAutoLogin() {
        return getBooleanProperty(AUTOLOGIN, AUTO_LOGIN_DEFAULT_FALUE);
    }

    public void setAutoLogin(boolean autologin) {
        setProperty(AUTOLOGIN, Boolean.toString(autologin));
    }

    public Date getCurrentDate() {
        return new Date(System.currentTimeMillis() + timeDelta);
    }

    public long getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(long timeDelta) {
        this.timeDelta = timeDelta;
    }

    public void setServerTZOffset(int offset) {
        serverTimezoneOffset = offset;
    }

    public int getServerTimezoneOffset() {
        return serverTimezoneOffset;
    }

    public String getStoragePath() {
        return getProperty(STORAGE_PATH, STORAGE_PATH_DEFAULT_FALUE);
    }

    public String getUpdateApplicationName() {
        return getProperty(UPDATE_APPLICATION_NAME, "");
    }

    public void setUpdateApplicationName(final String applicationName) {
        setProperty(UPDATE_APPLICATION_NAME, applicationName);
    }

    protected void initHosts() {
        final String hostProperty = getProperty(Settings.HOST);
        if (hostProperty != null) {
            hosts = Arrays.asList(StrUtil.split(hostProperty, ";"));
            currentHost = null;
            switchHost();
        } else {
            hosts = new ArrayList();//Collections.emptyList();
        }
    }

    public String getCurrentHost() {
        return currentHost;
    }

    public void switchHost() {
        if (hosts.size() > 0) {
            if (currentHost == null) {
                int res = (int) Math.round(Math.random() * (hosts.size() - 1));
                currentHost = (String) hosts.get(res);
            } else {
                currentHost = (String) hosts.get((hosts.indexOf(currentHost) + 1) % hosts.size());
            }
        }
    }

    public boolean getLoggingEnabled() {
        return getBooleanProperty(LOGGING_ENABLED, LOGGING_ENABLED_DEFAULT_VALUE);
    }

    public String getFrameTitleSuffix() {
        return getProperty(FRAME_TITLE_SUFFIX, FRAME_TITLE_SUFFIX_DEFAULT_FALUE);
    }

    public String getMcClientCoreVersion() {
        return "1.0.0";
    }

    public String getMcClientPlatformVersion() {
        return "1.0.0";
    }

    public boolean isOfflineVersion() {
        return getBooleanProperty(OFFLINE_VERSION, "false");
    }

    /**
     * Is need to use DemoStorageInitializer before login (for testing purposes)
     *
     * @return
     */
    public boolean needToInitializeStorage() {
        return false;
    }

    public void addProperty(String name, String value) {
        //Override this method for add property
    }

}
