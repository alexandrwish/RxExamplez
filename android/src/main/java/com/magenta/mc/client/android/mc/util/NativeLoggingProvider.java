package com.magenta.mc.client.android.mc.util;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

/**
 * Author: Petr Popov
 * Created: 27.01.2011 11:09:38
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public class NativeLoggingProvider {

    protected void error(String message) {
        MCLoggerFactory.getLogger(getClass()).error(message);
    }

    protected void info(String message) {
        MCLoggerFactory.getLogger(getClass()).info(message);
    }

    protected void debug(String message) {
        MCLoggerFactory.getLogger(getClass()).debug(message);
    }

    protected void trace(String message) {
        MCLoggerFactory.getLogger(getClass()).trace(message);
    }

}
