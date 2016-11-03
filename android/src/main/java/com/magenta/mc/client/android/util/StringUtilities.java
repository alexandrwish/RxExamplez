package com.magenta.mc.client.android.util;

/**
 * Project: mobile
 * Author:  Alexey Osipov
 * Created: 13.01.14
 * <p/>
 * Copyright (c) 1999-2014 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 */
public class StringUtilities {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String tag(Class clazz) {
        return clazz.getName();
    }
}
