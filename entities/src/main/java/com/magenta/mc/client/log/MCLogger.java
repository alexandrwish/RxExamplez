package com.magenta.mc.client.log;

import net.sf.microlog.core.Level;

/**
 * Author: Petr Popov
 * Created: 07.02.2011 15:07:43
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public interface MCLogger {

    void log(Level level, String msg);

    void log(Level level, String msg, Throwable t);

    void trace(Object message);

    void trace(Object message, Throwable t);

    void debug(Object message);

    void debug(Object message, Throwable t);

    void info(Object message);

    void info(Object message, Throwable t);

    void warn(Object message);

    void warn(Object message, Throwable t);

    void error(Object message);

    void error(Object message, Throwable t);
}
