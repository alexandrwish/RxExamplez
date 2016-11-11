package com.magenta.mc.client.settings;

import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.util.ResourceManager;
import com.magenta.mc.client.util.StrUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Settings extends Properties {

    public static final String HOST = "server.address";
    public static final String PORT = "server.port";
    public static final String KEEP_ALIVE_PERIOD = "keepAlive.period";
    public static final String KEEP_ALIVE_TYPE = "keepAlive.type";
    public static final String TIMEZONE_PROPERTY = "timezone";
    public static final String LOCALE_KEY = "locale.key";
    public static final String LOGGING_ENABLED = "logging.enabled";
    public static final String LOG_USING_ONE_LOGGER_PROPERTY = "logging.one.logger";
    public static final String RETRIEVE_INTERVAL_SEC = "tracking.retrieveIntervalSec";
    public static final String BATCH_COVER_SEC = "tracking.batchCoverSec";
    public static final String LOCATION_MAX_AGE = "tracking.locationMaxAgeSec";
    public static final String TRACKING_ENABLED = "tracking.enabled";

    private static final String USE_SSL = "useSSL";
    private static final String PLAIN_AUTH = "plainAuth";
    private static final String COMPRESSION = "compression";
    private static final String SERVER_NAME = "server.name";
    private static final String AUTOLOGIN = "autologin";
    private static final String STORAGE_PATH = "cache.file.path";
    private static final String AUTOAWAY = "autoaway";
    private static final String USER_ID = "user.id";
    private static final String SERVER_COMPONENT_NAME = "server.component.name";
    private static final String XMPP_RESOURCE = "xmpp.resource";
    private static final String LOGGING_FILE = "logging.file";
    private static final String LOGGING_CONSOLE = "logging.console";
    private static final String UPDATE_APPLICATION_NAME = "update.applicationName";
    private static final String OFFLINE_VERSION = "offline.version";
    private static final String FRAME_TITLE_SUFFIX = "frame.title.suffix";
    private static final String FILE_NAME = "settings.properties";
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
    private static File appFolder;
    private static File logFolder;

    protected String appVersion;
    protected String appName;

    private Properties inboundProperties = new Properties();
    private String password;
    private int devLaunch;
    private long timeDelta;
    private Set<PropertyEventListener> propertyListeners = new HashSet<>();
    private int serverTimezoneOffset;
    private List hosts;
    private String currentHost;

    protected Settings() {
        super();
        init();
    }

    protected Settings(Object context) {
        super();
        preInit(context);
        init();
    }

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
    private void initDefaultValues() {
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
    }

    private String getParentFolder() {
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
        return "\\Program Files";
    }

    private File getApplicationFolder() {
        if (appFolder == null) {
            return appFolder = new File(getParentFolder() + File.separator + appName + File.separator);
        } else {
            return appFolder;
        }
    }

    public File getLogFolder() {
        return logFolder == null ? (logFolder = getApplicationFolder()) : logFolder;
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

    private void init() {
        try {
            initDefaultValues();
            initAppNameAndVersion(); //load appName & and versions from jar, and then from file if exists
            getApplicationFolder(); // init appFolder property
            // load settings from jar first, overwriting default values
            inboundProperties.load(ResourceManager.getInstance().getResourceAsStream(getJarSettingsFileName()));
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

    private void loadSettingsFromFile() throws IOException {
        // Remove old settings
        File dir = getApplicationFolder();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                if (!child.equals(getSettingsFileName()) && child.startsWith("settings") && child.endsWith(".properties")) {
                    (new File(dir, child)).delete();
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

    private String getJarSettingsFileName() {
        return FILE_NAME;/**/
    }

    private String getSettingsFileName() {
        return FILE_NAME;/**/
    }

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
        for (PropertyEventListener listener : propertyListeners) {
            listener.propertyChanged(key, oldValue, newValue);
        }
    }

    public int getIntProperty(String key) {
        return getIntProperty(key, "0");
    }

    public int getIntProperty(String key, String defValue) {
        return Integer.valueOf(getProperty(key, defValue));
    }

    public boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, "false");
    }

    public boolean getBooleanProperty(String key, String defValue) {
        return Boolean.valueOf(getProperty(key, defValue).trim());
    }

    public long getLongProperty(String key, long defValue) {
        String val = getProperty(key);
        if (val != null) {
            try {
                return Long.valueOf(val);
            } catch (NumberFormatException ignored) {
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

    public String getUpdateApplicationName() {
        return getProperty(UPDATE_APPLICATION_NAME, "");
    }

    public void setUpdateApplicationName(final String applicationName) {
        setProperty(UPDATE_APPLICATION_NAME, applicationName);
    }

    private void initHosts() {
        final String hostProperty = getProperty(Settings.HOST);
        if (hostProperty != null) {
            hosts = Arrays.asList(StrUtil.split(hostProperty, ";"));
            currentHost = null;
            switchHost();
        } else {
            hosts = new ArrayList();
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

    public String getMcClientCoreVersion() {
        return "1.0.0";
    }

    public String getMcClientPlatformVersion() {
        return "1.0.0";
    }

    public boolean isOfflineVersion() {
        return getBooleanProperty(OFFLINE_VERSION, "false");
    }

    public boolean needToInitializeStorage() {
        return false;
    }
}