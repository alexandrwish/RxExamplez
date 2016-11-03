package com.magenta.maxunits.mobile.utils;

import android.util.Pair;

import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.mc.client.crypto.MD5;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

public class UserUtils {

    public static String createUserId(final String name, String account) {
        if (name == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        if (((MxSettings) Setup.get().getSettings()).isUseComponentNameAsUserPrefix()) {
            result.append(Settings.get().getServerComponentName()).append("-");
        }
        result.append(name);
        if (account != null && account.length() > 0) {
            result.append("-").append(account);
        }
        return result.toString();
    }

    public static String cutComponentName(final String name) {
        if (name == null) {
            return null;
        }
        final String compName = Settings.get().getServerComponentName() + "-";
        String withoutCompName = name;
        if (((MxSettings) Setup.get().getSettings()).isUseComponentNameAsUserPrefix() && name.startsWith(compName)) {
            withoutCompName = name.substring(compName.length());
        }
        return withoutCompName;
    }

    public static String stripUserId(final String name) {
        String withoutCompName = cutComponentName(name);
        int lastHyphen = withoutCompName.lastIndexOf("-");
        return (lastHyphen > -1) ? withoutCompName.substring(0, lastHyphen) : withoutCompName;
    }

    public static String stripAccount(final String name) {
        if (name == null) {
            return null;
        }
        int lastHyphen = name.lastIndexOf("-");
        return (lastHyphen > -1) ? name.substring(lastHyphen + 1) : null;
    }

    public static Pair<String, String> splitName(String name) {
        if (name == null) {
            return null;
        }
        int lastHyphen = name.lastIndexOf("-");
        Pair<String, String> result;
        if (lastHyphen > -1) {
            result = new Pair<String, String>(name.substring(0, lastHyphen), name.substring(lastHyphen + 1));
        } else {
            result = new Pair<String, String>("", name);
        }
        return result;
    }

    public static String encodeLoginPassword(final String password) {
        final MD5 md5 = new MD5();
        md5.init();
        md5.update(password.getBytes());
        md5.finish();
        return md5.getDigestHex();
    }
}