package com.magenta.mc.client.android.mc.log;

import android.support.annotation.NonNull;

import com.magenta.mc.client.android.mc.settings.PropertyEventListener;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.util.ResourceManager;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.LoggerFactory;
import net.sf.microlog.core.PropertyConfigurator;
import net.sf.microproperties.Properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class MCLoggerFactory {

    private static final Object instanceMutex = new Object();
    private static final Object initMutex = new Object();
    private static final String PROPERTIES_FILENAME = "microlog.properties";
    private static final String stdOutLoggerName = "STD OUT";
    private static final String stdErrLoggerName = "STD ERR";
    protected static MCLoggerFactory instance;
    protected MCLoggerDelegate delegate = new MCLoggerDelegate();
    private PrintStream systemOut = System.out;
    private PrintStream systemErr = System.err;
    private PrintStream loggerOutStream;
    private PrintStream loggerErrStream;
    private MCMemoryLogger beforeInitLogger = new MCMemoryLogger();
    private boolean isInitialized;
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

    private MCLogger getLogger1() {
        if (isInitialized) {
            return delegate.get(LoggerFactory.getLogger());
        } else {
            return beforeInitLogger.get();
        }
    }

    private MCLogger getLogger1(String name) {
        if (isInitialized) {
            if (Setup.get().getSettings().getBooleanProperty(Settings.LOG_USING_ONE_LOGGER_PROPERTY, "false")) {
                name = "MCLogger";
            }
            return delegate.get(LoggerFactory.getLogger(name));
        } else {
            return beforeInitLogger.get(name);
        }
    }

    private MCLogger getLogger1(Class clazz) {
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

    private void initLogging(Properties micrologProperties) {
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

    private void pipeStdToLogger() {
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

    private PrintStream redirectStreamToLogger(PrintStream stream, final String loggerName, final Level level) {
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

            public void write(@NonNull byte[] b, int off, int len) {
                if (recursionCheck.isRecursion()) {
                    if (!issuedWarning) {
                        String msg = "ERROR: invalid console appender config detected, console systemOut is looping";
                        try {
                            out.write(msg.getBytes());
                        } catch (IOException ignore) {
                        }
                        issuedWarning = true;
                    }
                } else {
                    while (len > 0 && (b[len - 1] == '\n' || b[len - 1] == '\r') && len > off) {
                        len--;
                    }
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

    public Properties getProperties() {
        return properties;
    }
}