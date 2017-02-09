package com.magenta.mc.client.android.util;

public class StringUtilities {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String tag(Class clazz) {
        return clazz.getName();
    }
}