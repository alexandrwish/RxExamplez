package com.magenta.mc.client.android.mc.locale;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.util.StringLoader;

import java.util.Hashtable;

public class SR {

    public static String MS_XMLLANG;
    private static Hashtable lang;
    public static String MS_CONNECT_TO = loadString("Connect.to");
    public static String MS_ERROR_ = loadString("Error:");
    public static String MS_SEND = loadString("Send");
    public static String MS_CLIENT_INFO = loadString("Client.Version");
    public static String MS_LOGIN_FAILED = loadString("Login failed");
    public static String MS_DISCONNECTED = loadString("Disconnected");
    public static String MS_IS_INVITING_YOU = loadString(" is inviting You to ");
    public static String MS_NO_VERSION_AVAILABLE = loadString("No client version available");
    public static String MS_OPENING_STREAM = loadString("Opening stream");
    public static String MS_ZLIB = loadString("Using compression");
    public static String MS_AUTH = loadString("Authenticating");
    public static String MS_RESOURCE_BINDING = loadString("Resource binding");
    public static String MS_SESSION = loadString("Initiating session");
    public static String MS_HAS_CHANGED_SUBJECT_TO = loadString(" has changed subject to: ");

    private SR() {
    }

    private synchronized static void loadLang() {
        if (lang == null) {
            String langFile = "";
            MCLoggerFactory.getLogger(SR.class).debug("Loading locale " + langFile);
            lang = new StringLoader().hashtableLoader(langFile);
            if (lang == null) lang = new Hashtable();
            MS_XMLLANG = (String) lang.get("xmlLang");
            String MS_IFACELANG = MS_XMLLANG;
            if (MS_IFACELANG == null) MS_IFACELANG = "en";
        }
    }

    private static String loadString(String key) {
        if (lang == null) loadLang();
        String value = (String) lang.get(key);
        if (value == null) {
            if (!lang.isEmpty()) {
                System.out.print("Can't find local string for <");
                System.err.print(key);
                System.err.println('>');
            }
        }
        return (value == null) ? key : value;
    }

    public static void loaded() {
        lang = null;
    }
}