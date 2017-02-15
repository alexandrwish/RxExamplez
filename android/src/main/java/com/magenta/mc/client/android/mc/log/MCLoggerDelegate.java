package com.magenta.mc.client.android.mc.log;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.Logger;

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