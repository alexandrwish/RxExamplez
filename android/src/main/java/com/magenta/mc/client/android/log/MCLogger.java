package com.magenta.mc.client.android.log;

import net.sf.microlog.core.Level;

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