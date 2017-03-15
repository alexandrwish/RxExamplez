package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.mc.crypto.MD5;

public class UserUtils {

    public static String encodeLoginPassword(final String password) {
        final MD5 md5 = new MD5();
        md5.init();
        md5.update(password.getBytes());
        md5.finish();
        return md5.getDigestHex();
    }
}