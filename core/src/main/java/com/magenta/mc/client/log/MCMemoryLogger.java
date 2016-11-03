package com.magenta.mc.client.log;

import net.sf.microlog.core.Level;

import java.util.ArrayList;

/**
 * Author: Petr Popov
 * Created: 07.02.2011 15:16:41
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public class MCMemoryLogger implements MCLogger {

    private ThreadLocal loggerName = new ThreadLocal();
    private ArrayList messages = new ArrayList();
    private ArrayList names = new ArrayList();
    private ArrayList levels = new ArrayList();
    private ArrayList throwables = new ArrayList();

    public MCMemoryLogger get(Class clazz) {
        loggerName.set(clazz);
        return this;
    }

    public MCMemoryLogger get(String name) {
        loggerName.set(name);
        return this;
    }

    public MCMemoryLogger get() {
        loggerName.set(null);
        return this;
    }

    public void log(Level level, String message, Throwable t) {
        names.add(loggerName.get());
        messages.add(message);
        levels.add(level);
        throwables.add(t);
    }

    private void clear() {
        names.clear();
        messages.clear();
        levels.clear();
    }

    public void log(Level level, String msg) {
        log(level, msg, null);
    }

    public void trace(Object message) {
        log(Level.TRACE, message.toString(), null);
    }

    public void trace(Object message, Throwable t) {
        log(Level.TRACE, message.toString(), t);
    }

    public void debug(Object message) {
        log(Level.DEBUG, message.toString(), null);
    }

    public void debug(Object message, Throwable t) {
        log(Level.DEBUG, message.toString(), t);
    }

    public void info(Object message) {
        log(Level.INFO, message.toString(), null);
    }

    public void info(Object message, Throwable t) {
        log(Level.INFO, message.toString(), t);
    }

    public void warn(Object message) {
        log(Level.WARN, message.toString(), null);
    }

    public void warn(Object message, Throwable t) {
        log(Level.WARN, message.toString(), t);
    }

    public void error(Object message) {
        log(Level.ERROR, message.toString(), null);
    }

    public void error(Object message, Throwable t) {
        log(Level.ERROR, message.toString(), null);
    }

    void flush() {
        if (messages.size() > 0) {
            for (int i = 0; i < Math.max(messages.size(), levels.size()); i++) {
                Level level = (Level) levels.get(i);
                String message = (String) messages.get(i);
                MCLogger logger = null;
                if (names.get(i) == null) {
                    logger = MCLoggerFactory.getInstance().getLogger();
                } else if (names.get(i) instanceof String) {
                    logger = MCLoggerFactory.getInstance().getLogger((String) names.get(i));
                } else if (names.get(i) instanceof Class) {
                    logger = MCLoggerFactory.getInstance().getLogger((Class) names.get(i));
                } else {
                    logger = MCLoggerFactory.getInstance().getLogger(getClass());
                    logger.warn("Wrong logger name: " + names.get(i).toString());
                }

                logger.log(level, message, (Throwable) throwables.get(i));
            }
            clear();
        }
    }
}
