package com.magenta.mc.client.android.log;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.Logger;

public class MCLoggerDelegate implements MCLogger {

    private ThreadLocal<Logger> logger = new ThreadLocal<>();

    public MCLoggerDelegate get(Logger logger) {
        this.logger.set(logger);
        return this;
    }

    public void log(Level level, String msg) {
        logger.get().log(level, msg);
    }

    public void log(Level level, String msg, Throwable t) {
        logger.get().log(level, msg, t);
    }

    public void trace(Object message) {
        logger.get().trace(message);
    }

    public void trace(Object message, Throwable t) {
        logger.get().trace(message, t);
    }

    public void debug(Object message) {
        logger.get().debug(message);
    }

    public void debug(Object message, Throwable t) {
        logger.get().debug(message, t);
    }

    public void info(Object message) {
        logger.get().info(message);
    }

    public void info(Object message, Throwable t) {
        logger.get().info(message, t);
    }

    public void warn(Object message) {
        logger.get().warn(message);
    }

    public void warn(Object message, Throwable t) {
        logger.get().warn(message, t);
    }

    public void error(Object message) {
        logger.get().error(message);
    }

    public void error(Object message, Throwable t) {
        logger.get().error(message, t);
    }
}