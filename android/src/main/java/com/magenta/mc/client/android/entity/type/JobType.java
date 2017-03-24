package com.magenta.mc.client.android.entity.type;

import com.magenta.mc.client.android.exception.UnknownJobStatusException;

public enum JobType {

    UNKNOWN(-1),
    TRANSFER(0),
    BREAK(1);

    private static final String STR_TRANSFER = "RUN";
    private static final String STR_BREAK = "BREAK";
    private final int type;


    JobType(int type) {
        this.type = type;
    }

    public static String stringValue(JobType type) throws UnknownJobStatusException {
        switch (type) {
            case TRANSFER: {
                return STR_TRANSFER;
            }
            case BREAK: {
                return STR_BREAK;
            }
        }
        throw new UnknownJobStatusException("Unknown job type: " + type);
    }

    public static JobType valueOf(int value) {
        switch (value) {
            case -1: {
                return UNKNOWN;
            }
            case 0: {
                return TRANSFER;
            }
            case 1: {
                return BREAK;
            }
        }
        return UNKNOWN;
    }
}