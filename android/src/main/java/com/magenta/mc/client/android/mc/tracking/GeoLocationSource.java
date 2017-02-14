package com.magenta.mc.client.android.mc.tracking;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author Sergey Grachev
 */
public class GeoLocationSource implements Serializable {

    public static final GeoLocationSource UNKNOWN = new GeoLocationSource("UNKNOWN");
    public static final GeoLocationSource GPS = new GeoLocationSource("GPS");
    public static final GeoLocationSource LBS = new GeoLocationSource("LBS");
    private static final long serialVersionUID = 8139833965403961228L;
    private final String type;

    private GeoLocationSource(final String type) {
        this.type = type;
    }

    public static GeoLocationSource valueOf(final String state) {
        if (state.equalsIgnoreCase(UNKNOWN.toString())) {
            return UNKNOWN;
        } else if (state.equalsIgnoreCase(GPS.toString())) {
            return GPS;
        } else if (state.equalsIgnoreCase(LBS.toString())) {
            return LBS;
        }
        throw new RuntimeException("Invalid GeoLocationSource: " + state);
    }

    public String toString() {
        return type;
    }

    private Object readResolve() throws ObjectStreamException {
        if (type.equalsIgnoreCase("UNKNOWN")) {
            return UNKNOWN;
        } else if (type.equalsIgnoreCase("GPS")) {
            return GPS;
        } else if (type.equalsIgnoreCase("LBS")) {
            return LBS;
        }
        throw new RuntimeException("Invalid GeoLocationSource: " + type);
    }
}
