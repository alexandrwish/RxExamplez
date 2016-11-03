package com.magenta.mc.client.tracking;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @autor Petr Popov
 * Created 12.04.12 12:59
 */
public class GeoLocationState implements Serializable {

    public static final GeoLocationState UNKNOWN = new GeoLocationState("UNKNOWN");
    public static final GeoLocationState MOTIONLESS = new GeoLocationState("MOTIONLESS");
    public static final GeoLocationState IN_MOTION = new GeoLocationState("IN_MOTION");
    public static final GeoLocationState STOPPING = new GeoLocationState("STOPPING");
    public static final GeoLocationState STARTING = new GeoLocationState("STARTING");
    private static final long serialVersionUID = 1;
    private String type;

    private GeoLocationState(String type) {
        this.type = type;
    }

    public static GeoLocationState valueOf(String state) {
        if (state.equalsIgnoreCase(UNKNOWN.toString())) {
            return UNKNOWN;
        } else if (state.equalsIgnoreCase(MOTIONLESS.toString())) {
            return MOTIONLESS;
        } else if (state.equalsIgnoreCase(IN_MOTION.toString())) {
            return IN_MOTION;
        } else if (state.equalsIgnoreCase(STOPPING.toString())) {
            return STOPPING;
        } else if (state.equalsIgnoreCase(STARTING.toString())) {
            return STARTING;
        }
        throw new RuntimeException("Invalid GeoLocationState: " + state);
    }

    public String toString() {
        return type;
    }

    private Object readResolve() throws ObjectStreamException {
        if (type.equalsIgnoreCase("UNKNOWN")) {
            return UNKNOWN;
        } else if (type.equalsIgnoreCase("MOTIONLESS")) {
            return MOTIONLESS;
        } else if (type.equalsIgnoreCase("IN_MOTION")) {
            return IN_MOTION;
        } else if (type.equalsIgnoreCase("STOPPING")) {
            return STOPPING;
        } else if (type.equalsIgnoreCase("STARTING")) {
            return STARTING;
        }
        throw new RuntimeException("Invalid GeoLocationState: " + type);
    }
}
