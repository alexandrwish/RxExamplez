package com.magenta.mc.client.android.entity;

public class TaskState {

    public static final int UNKNOWN = -1;
    public static final String STR_UNKNOWN = "UNKNOWN";

    public static final int PRE_CANCELLED = 1;
    public static final int PRE_COA = 2;
    public static final int COA = 3;

    // run states
    public static final int RUN_UNASSIGNED = 100;
    public static final int RUN_ASSIGNED = 101;
    public static final int RUN_SENT = 102;
    public static final int RUN_RECEIVED = 103;
    public static final int RUN_ACCEPTED = 104;
    public static final int RUN_REJECTED = 105;
    public static final int RUN_STARTED = 106;
    public static final int RUN_IN_PROGRESS = 107;
    public static final int RUN_FINISHED = 108;
    public static final int RUN_COMPLETED = 109;
    public static final int RUN_CANCELLED = 110;
    public static final int RUN_ABORTED = 111;
    public static final int RUN_LATE15 = 112;
    public static final int RUN_LATE30 = 113;
    public static final int RUN_LATE60 = 114;
    public static final int RUN_LOCATION_REACHED = 115;
    // stop states
    public static final int STOP_IDLE = 200;
    public static final int STOP_RUN_ACCEPTED = 201;
    public static final int STOP_RUN_STARTED = 202;
    public static final int STOP_ON_ROUTE = 203;
    public static final int STOP_ARRIVED = 204;
    public static final int STOP_ARRIVE5 = 205;
    public static final int STOP_ARRIVE10 = 206;
    public static final int STOP_DROP5 = 207;
    public static final int STOP_DROP10 = 208;
    public static final int STOP_LATE15 = 209;
    public static final int STOP_LATE30 = 210;
    public static final int STOP_LATE60 = 211;
    public static final int STOP_LATE_A_LOT = 212;
    public static final int STOP_RUN_FINISHED = 213;
    public static final int STOP_COMPLETED = 214;
    public static final int STOP_FAIL = 215;
    public static final int STOP_ABORTED = 216;
    public static final int STOP_SUSPENDED = 217;
    public static final String STR_STOP_IDLE = "STOP_IDLE";
    public static final String STR_STOP_RUN_ACCEPTED = "STOP_RUN_ACCEPTED";
    public static final String STR_STOP_RUN_STARTED = "STOP_RUN_STARTED";
    public static final String STR_STOP_ON_ROUTE = "STOP_ON_ROUTE";
    public static final String STR_STOP_ARRIVED = "STOP_ARRIVED";
    public static final String STR_STOP_ARRIVE5 = "STOP_ARRIVE5";
    public static final String STR_STOP_ARRIVE10 = "STOP_ARRIVE10";
    public static final String STR_STOP_DROP5 = "STOP_DROP5";
    public static final String STR_STOP_DROP10 = "STOP_DROP10";
    public static final String STR_STOP_LATE15 = "STOP_LATE15";
    public static final String STR_STOP_LATE30 = "STOP_LATE30";
    public static final String STR_STOP_LATE60 = "STOP_LATE60";
    public static final String STR_STOP_LATE_A_LOT = "STOP_LATE_A_LOT";
    public static final String STR_STOP_RUN_FINISHED = "STOP_RUN_FINISHED";
    public static final String STR_STOP_COMPLETED = "STOP_COMPLETED";
    public static final String STR_STOP_FAIL = "STOP_FAIL";
    public static final String STR_STOP_ABORTED = "STOP_ABORTED";
    public static final String STR_STOP_SUSPENDED = "STOP_SUSPENDED";

    private static final String STR_RUN_UNASSIGNED = "RUN_UNASSIGNED";
    private static final String STR_RUN_ASSIGNED = "RUN_ASSIGNED";
    private static final String STR_RUN_SENT = "RUN_SENT";
    private static final String STR_RUN_RECEIVED = "RUN_RECEIVED";
    private static final String STR_RUN_ACCEPTED = "RUN_ACCEPTED";
    private static final String STR_RUN_REJECTED = "RUN_REJECTED";
    private static final String STR_RUN_STARTED = "RUN_STARTED";
    private static final String STR_RUN_IN_PROGRESS = "RUN_IN_PROGRESS";
    private static final String STR_RUN_FINISHED = "RUN_FINISHED";
    private static final String STR_RUN_COMPLETED = "RUN_COMPLETED";
    private static final String STR_RUN_CANCELLED = "RUN_CANCELLED";
    private static final String STR_RUN_ABORTED = "RUN_ABORTED";
    private static final String STR_RUN_LATE15 = "RUN_LATE15";
    private static final String STR_RUN_LATE30 = "RUN_LATE30";
    private static final String STR_RUN_LATE60 = "RUN_LATE60";
    private static final String STR_RUN_LOCATION_REACHED = "RUN_LOCATION_REACHED";

    TaskState() {
    }

    public static String stringValue(final int status) {
        switch (status) {
            // run states
            case RUN_UNASSIGNED:
                return STR_RUN_UNASSIGNED;
            case RUN_ASSIGNED:
                return STR_RUN_ASSIGNED;
            case RUN_SENT:
                return STR_RUN_SENT;
            case RUN_RECEIVED:
                return STR_RUN_RECEIVED;
            case RUN_ACCEPTED:
                return STR_RUN_ACCEPTED;
            case RUN_REJECTED:
                return STR_RUN_REJECTED;
            case RUN_STARTED:
                return STR_RUN_STARTED;
            case RUN_IN_PROGRESS:
                return STR_RUN_IN_PROGRESS;
            case RUN_COMPLETED:
                return STR_RUN_COMPLETED;
            case RUN_FINISHED:
                return STR_RUN_FINISHED;
            case RUN_CANCELLED:
                return STR_RUN_CANCELLED;
            case RUN_ABORTED:
                return STR_RUN_ABORTED;
            case RUN_LATE15:
                return STR_RUN_LATE15;
            case RUN_LATE30:
                return STR_RUN_LATE30;
            case RUN_LATE60:
                return STR_RUN_LATE60;
            case RUN_LOCATION_REACHED:
                return STR_RUN_LOCATION_REACHED;
            // stop states
            case STOP_IDLE:
                return STR_STOP_IDLE;
            case STOP_RUN_ACCEPTED:
                return STR_STOP_RUN_ACCEPTED;
            case STOP_RUN_STARTED:
                return STR_STOP_RUN_STARTED;
            case STOP_ON_ROUTE:
                return STR_STOP_ON_ROUTE;
            case STOP_ARRIVED:
                return STR_STOP_ARRIVED;
            case STOP_ARRIVE5:
                return STR_STOP_ARRIVE5;
            case STOP_ARRIVE10:
                return STR_STOP_ARRIVE10;
            case STOP_DROP5:
                return STR_STOP_DROP5;
            case STOP_DROP10:
                return STR_STOP_DROP10;
            case STOP_LATE15:
                return STR_STOP_LATE15;
            case STOP_LATE30:
                return STR_STOP_LATE30;
            case STOP_LATE60:
                return STR_STOP_LATE60;
            case STOP_LATE_A_LOT:
                return STR_STOP_LATE_A_LOT;
            case STOP_RUN_FINISHED:
                return STR_STOP_RUN_FINISHED;
            case STOP_COMPLETED:
                return STR_STOP_COMPLETED;
            case STOP_FAIL:
                return STR_STOP_FAIL;
            case STOP_ABORTED:
                return STR_STOP_ABORTED;
            case STOP_SUSPENDED:
                return STR_STOP_SUSPENDED;
        }
        return STR_UNKNOWN;
    }

    public static int intValue(final String status) {
        // run states
        if (STR_RUN_UNASSIGNED.equalsIgnoreCase(status)) {
            return RUN_UNASSIGNED;
        }
        if (STR_RUN_ASSIGNED.equalsIgnoreCase(status)) {
            return RUN_ASSIGNED;
        }
        if (STR_RUN_SENT.equalsIgnoreCase(status)) {
            return RUN_SENT;
        }
        if (STR_RUN_RECEIVED.equalsIgnoreCase(status)) {
            return RUN_RECEIVED;
        }
        if (STR_RUN_ACCEPTED.equalsIgnoreCase(status)) {
            return RUN_ACCEPTED;
        }
        if (STR_RUN_REJECTED.equalsIgnoreCase(status)) {
            return RUN_REJECTED;
        }
        if (STR_RUN_STARTED.equalsIgnoreCase(status)) {
            return RUN_STARTED;
        }
        if (STR_RUN_IN_PROGRESS.equalsIgnoreCase(status)) {
            return RUN_IN_PROGRESS;
        }
        if (STR_RUN_FINISHED.equalsIgnoreCase(status)) {
            return RUN_FINISHED;
        }
        if (STR_RUN_COMPLETED.equalsIgnoreCase(status)) {
            return RUN_COMPLETED;
        }
        if (STR_RUN_CANCELLED.equalsIgnoreCase(status)) {
            return RUN_CANCELLED;
        }
        if (STR_RUN_ABORTED.equalsIgnoreCase(status)) {
            return RUN_ABORTED;
        }
        if (STR_RUN_LATE15.equalsIgnoreCase(status)) {
            return RUN_LATE15;
        }
        if (STR_RUN_LATE30.equalsIgnoreCase(status)) {
            return RUN_LATE30;
        }
        if (STR_RUN_LATE60.equalsIgnoreCase(status)) {
            return RUN_LATE60;
        }
        if (STR_RUN_LOCATION_REACHED.equalsIgnoreCase(status)) {
            return RUN_LOCATION_REACHED;
        }
        // stop states
        if (STR_STOP_IDLE.equalsIgnoreCase(status)) {
            return STOP_IDLE;
        }
        if (STR_STOP_RUN_ACCEPTED.equalsIgnoreCase(status)) {
            return STOP_RUN_ACCEPTED;
        }
        if (STR_STOP_RUN_STARTED.equalsIgnoreCase(status)) {
            return STOP_RUN_STARTED;
        }
        if (STR_STOP_ON_ROUTE.equalsIgnoreCase(status)) {
            return STOP_ON_ROUTE;
        }
        if (STR_STOP_ARRIVED.equalsIgnoreCase(status)) {
            return STOP_ARRIVED;
        }
        if (STR_STOP_ARRIVE5.equalsIgnoreCase(status)) {
            return STOP_ARRIVE5;
        }
        if (STR_STOP_ARRIVE10.equalsIgnoreCase(status)) {
            return STOP_ARRIVE10;
        }
        if (STR_STOP_DROP5.equalsIgnoreCase(status)) {
            return STOP_DROP5;
        }
        if (STR_STOP_DROP10.equalsIgnoreCase(status)) {
            return STOP_DROP10;
        }
        if (STR_STOP_LATE15.equalsIgnoreCase(status)) {
            return STOP_LATE15;
        }
        if (STR_STOP_LATE30.equalsIgnoreCase(status)) {
            return STOP_LATE30;
        }
        if (STR_STOP_LATE60.equalsIgnoreCase(status)) {
            return STOP_LATE60;
        }
        if (STR_STOP_LATE_A_LOT.equalsIgnoreCase(status)) {
            return STOP_LATE_A_LOT;
        }
        if (STR_STOP_RUN_FINISHED.equalsIgnoreCase(status)) {
            return STOP_RUN_FINISHED;
        }
        if (STR_STOP_FAIL.equalsIgnoreCase(status)) {
            return STOP_FAIL;
        }
        if (STR_STOP_COMPLETED.equalsIgnoreCase(status)) {
            return STOP_COMPLETED;
        }
        if (STR_STOP_ABORTED.equalsIgnoreCase(status)) {
            return STOP_ABORTED;
        }
        if (STR_STOP_SUSPENDED.equalsIgnoreCase(status)) {
            return STOP_SUSPENDED;
        }
        return UNKNOWN;
    }
}