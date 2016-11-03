package com.magenta.mc.client.log;

import com.magenta.mc.client.settings.PropertyEventListener;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.ResourceManager;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.LoggerFactory;
import net.sf.microlog.core.PropertyConfigurator;
import net.sf.microproperties.Properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Author: Petr Popov
 * Created: 27.01.2011 11:47:16
 * <p/>
 * Copyright (c) 1999-2010 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 */
public class MCLoggerFactory {
    private static final Object instanceMutex = new Object();
    private static final Object initMutex = new Object();
    protected static MCLoggerFactory instance;
    private final String PROPERTIES_FILENAME = "microlog.properties";
    protected String stdOutLoggerName = "STD OUT";
    protected String stdErrLoggerName = "STD ERR";
    protected PrintStream systemOut = System.out;
    protected PrintStream systemErr = System.err;
    protected PrintStream loggerOutStream;
    protected PrintStream loggerErrStream;
    protected MCLoggerDelegate delegate = new MCLoggerDelegate();
    protected MCMemoryLogger beforeInitLogger = new MCMemoryLogger();
    protected boolean isInitialized;
    private Properties properties;

    protected MCLoggerFactory() {
    }

    public static MCLoggerFactory getInstance() {
        if (instance == null) {
            synchronized (instanceMutex) {
                if (instance == null) {
                    instance = new MCLoggerFactory();
                }
            }
        }
        return instance;
    }

    public static MCLogger getLogger() {
        return getInstance().getLogger1();
    }

    public static MCLogger getLogger(String name) {
        return getInstance().getLogger1(name);
    }

    public static MCLogger getLogger(Class clazz) {
        return getInstance().getLogger1(clazz);
    }

    public boolean isLoggingInitialized() {
        return isInitialized;
    }

    protected MCLogger getLogger1() {
        if (isInitialized) {
            return delegate.get(LoggerFactory.getLogger());
        } else {
            return beforeInitLogger.get();
        }
    }

    protected MCLogger getLogger1(String name) {
        if (isInitialized) {
            if (Setup.get().getSettings().getBooleanProperty(Settings.LOG_USING_ONE_LOGGER_PROPERTY, "false")) {
                name = "MCLogger";
            }
            return delegate.get(LoggerFactory.getLogger(name));
        } else {
            return beforeInitLogger.get(name);
        }
    }

    protected MCLogger getLogger1(Class clazz) {
        if (isInitialized) {
            if (Setup.get().getSettings().getBooleanProperty(Settings.LOG_USING_ONE_LOGGER_PROPERTY, "false")) {
                clazz = MCLogger.class;
            }
            return delegate.get(LoggerFactory.getLogger(clazz));
        } else {
            return beforeInitLogger.get(clazz);
        }
    }

    public void initLogging() {
        initLogging(null);
    }

    public void initLogging(Properties micrologProperties) {
        if (!isInitialized) {
            synchronized (initMutex) {
                if (!isInitialized) {
                    try {
                        if (micrologProperties == null) {
                            micrologProperties = new Properties();
                            InputStream propStream = Setup.get().getSettings().openFile(PROPERTIES_FILENAME);
                            if (propStream == null) { // no explicit file, using properties bound to jar
                                propStream = ResourceManager.getInstance().getResourceAsStream(PROPERTIES_FILENAME);
                            }
                            micrologProperties.load(propStream);
                        }
                        properties = micrologProperties;
                        new PropertyConfigurator().configure(micrologProperties);
                    } catch (Exception e) {
                        getLogger(getClass()).error("Error while loading microlog properties from " + PROPERTIES_FILENAME, e);
                    }
                    Setup.get().getSettings().addPropertyListener(new PropertyEventListener() {
                        public void propertyChanged(String property, String oldValue, String newValue) {
                            if (Settings.LOGGING_ENABLED.equals(property)) {
                                pipeStdToLogger();
                            }
                        }
                    });
                    pipeStdToLogger();
                    isInitialized = true;
                    beforeInitLogger.flush();
                    getLogger(getClass()).info("logging initialized");
                }
            }
        }
    }

    protected void pipeStdToLogger() {
        if (Setup.get().getSettings().getLoggingEnabled()) {
            if (loggerOutStream == null) {
                loggerOutStream = redirectStreamToLogger(systemOut, stdOutLoggerName, Level.DEBUG);
            }
            if (loggerErrStream == null) {
                loggerErrStream = redirectStreamToLogger(systemErr, stdErrLoggerName, Level.ERROR);
            }
            System.setOut(loggerOutStream);
            System.setErr(loggerErrStream);
        } else {
            System.setOut(systemOut);
            System.setErr(systemErr);
        }
    }

    protected PrintStream redirectStreamToLogger(PrintStream stream, final String loggerName, final Level level) {
        return new PrintStream(stream) {
            private RecursionCheck recursionCheck = new RecursionCheck();
            private boolean issuedWarning;

            public void println(String msg) {
                if (msg == null)
                    msg = "null";
                byte[] bytes = msg.getBytes();
                write(bytes, 0, bytes.length);
            }

            public void println(Object msg) {
                if (msg == null)
                    msg = "null";
                byte[] bytes = msg.toString().getBytes();
                write(bytes, 0, bytes.length);
            }

            public void write(byte b) {
                byte[] bytes = {b};
                write(bytes, 0, 1);
            }

            public void write(byte[] b, int off, int len) {
                if (recursionCheck.isRecursion()) {
                    /* There is a configuration error that is causing looping. Most
                       likely there are two console appenders so just return to prevent
                       spinning.
                    */
                    if (!issuedWarning) {
                        String msg = "ERROR: invalid console appender config detected, console systemOut is looping";
                        try {
                            out.write(msg.getBytes());
                        } catch (IOException ignore) {
                        }
                        issuedWarning = true;
                    }
                } else {
                    // Remove the end of line chars
                    while (len > 0 && (b[len - 1] == '\n' || b[len - 1] == '\r') && len > off)
                        len--;

                    if (len != 0) {
                        final String msg = new String(b, off, len);
                        recursionCheck.execute(new Runnable() {
                            public void run() {
                                if (Level.ERROR.equals(level)) {
                                    MCLoggerFactory.getLogger(loggerName).error(msg);
                                } else if (Level.WARN.equals(level)) {
                                    MCLoggerFactory.getLogger(loggerName).warn(msg);
                                } else if (Level.INFO.equals(level)) {
                                    MCLoggerFactory.getLogger(loggerName).info(msg);
                                } else if (Level.DEBUG.equals(level)) {
                                    MCLoggerFactory.getLogger(loggerName).debug(msg);
                                }
                            }
                        });
                    }
                }
            }
        };
    }

    public PrintStream getSystemOut() {
        return systemOut;
    }

    public PrintStream getSystemErr() {
        return systemErr;
    }

    public Properties getProperties() {
        return properties;
    }
}
