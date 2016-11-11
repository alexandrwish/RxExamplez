package com.magenta.mc.client.log;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.Logger;

/**
 * Author: Petr Popov
 * Created: 07.02.2011 15:32:43
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public class MCLoggerDelegate implements MCLogger {

    private ThreadLocal logger = new ThreadLocal();

    public MCLoggerDelegate get(Logger logger) {
        this.logger.set(logger);
        return this;
    }

    public void log(Level level, String msg) {
        ((Logger) logger.get()).log(level, msg);
    }

    public void log(Level level, String msg, Throwable t) {
        ((Logger) logger.get()).log(level, msg, t);
    }

    public void trace(Object message) {
        ((Logger) logger.get()).trace(message);
    }

    public void trace(Object message, Throwable t) {
        ((Logger) logger.get()).trace(message, t);
    }

    public void debug(Object message) {
        ((Logger) logger.get()).debug(message);
    }

    public void debug(Object message, Throwable t) {
        ((Logger) logger.get()).debug(message, t);
    }

    public void info(Object message) {
        ((Logger) logger.get()).info(message);
    }

    public void info(Object message, Throwable t) {
        ((Logger) logger.get()).info(message, t);
    }

    public void warn(Object message) {
        ((Logger) logger.get()).warn(message);
    }

    public void warn(Object message, Throwable t) {
        ((Logger) logger.get()).warn(message, t);
    }

    public void error(Object message) {
        ((Logger) logger.get()).error(message);
    }

    public void error(Object message, Throwable t) {
        ((Logger) logger.get()).error(message, t);
    }

}
