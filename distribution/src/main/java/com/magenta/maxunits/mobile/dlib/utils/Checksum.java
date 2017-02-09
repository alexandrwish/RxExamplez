package com.magenta.maxunits.mobile.dlib.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Checksum {

    private Checksum() {
    }

    public static String md5(final String s) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            return Base64.encodeToString(digest.digest(), Base64.NO_PADDING | Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}