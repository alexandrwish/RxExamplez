package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.crypto.MD5;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.setup.Setup;

public class UserUtils {

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

    public static String encodeLoginPassword(final String password) {
        final MD5 md5 = new MD5();
        md5.init();
        md5.update(password.getBytes());
        md5.finish();
        return md5.getDigestHex();
    }
}