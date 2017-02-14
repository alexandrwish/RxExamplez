package com.magenta.mc.client.android.entity;

import com.magenta.mc.client.android.mc.exception.UnknownJobStatusException;

public class JobType {

    public static final int UNKNOWN = -1;
    public static final int TRANSFER = 0;
    public static final int BREAK = 1;

    private static final String STR_TRANSFER = "RUN";
    private static final String STR_BREAK = "BREAK";

    public static String stringValue(int type) throws UnknownJobStatusException {
        switch (type) {
            case TRANSFER:
                return STR_TRANSFER;
            case BREAK:
                return STR_BREAK;
        }
        throw new UnknownJobStatusException("Unknown job type: " + type);
    }

    public static int fromString(String type) {
        if (type == null || type.length() == 0) {
            return UNKNOWN;
        }
        if (type.equalsIgnoreCase(STR_TRANSFER)) {
            return TRANSFER;
        } else if (type.equalsIgnoreCase(STR_BREAK)) {
            return BREAK;
        } else {
            return UNKNOWN;
        }
    }
}