package com.magenta.mc.client.android.mc.log_sending;

public class LogType {

    public static final LogType APP = new LogType("APP");
    public static final LogType LAUNCHER = new LogType("LAUNCHER");
    public static final LogType UPDATER = new LogType("UPDATER");
    public static final LogType INSTALLER = new LogType("INSTALLER");

    private String type;

    private LogType(String type) {
        this.type = type;
    }

    public static LogType valueOf(String type) {
        if (type.equalsIgnoreCase(APP.toString())) {
            return APP;
        } else if (type.equalsIgnoreCase(LAUNCHER.toString())) {
            return LAUNCHER;
        } else if (type.equalsIgnoreCase(UPDATER.toString())) {
            return UPDATER;
        } else if (type.equalsIgnoreCase(INSTALLER.toString())) {
            return INSTALLER;
        }
        throw new RuntimeException("Invalid log type: " + type);
    }

    public String toString() {
        return type;
    }
}