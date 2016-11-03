package com.magenta.maxunits.mobile.utils;

import android.content.Context;

import com.magenta.maxunits.mx.R;

import static com.magenta.maxunits.mobile.entity.TaskState.RUN_ABORTED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_ACCEPTED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_ASSIGNED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_CANCELLED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_COMPLETED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_FINISHED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_IN_PROGRESS;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_LATE15;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_LATE30;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_LATE60;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_LOCATION_REACHED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_RECEIVED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_REJECTED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_SENT;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_STARTED;
import static com.magenta.maxunits.mobile.entity.TaskState.RUN_UNASSIGNED;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_ABORTED;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_ARRIVE10;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_ARRIVE5;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_ARRIVED;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_COMPLETED;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_DROP10;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_DROP5;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_FAIL;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_IDLE;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_LATE15;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_LATE30;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_LATE60;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_LATE_A_LOT;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_ON_ROUTE;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_RUN_ACCEPTED;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_RUN_FINISHED;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_RUN_STARTED;
import static com.magenta.maxunits.mobile.entity.TaskState.STOP_SUSPENDED;

/**
 * @author Sergey Grachev
 */
public final class StatusUtils {

    private StatusUtils() {
    }

    public static String translate(final Context context, final int state) {
        switch (state) {
            case RUN_UNASSIGNED:
                return context.getString(R.string.states_run_unassigned);
            case RUN_ASSIGNED:
                return context.getString(R.string.states_run_assigned);
            case RUN_SENT:
                return context.getString(R.string.states_run_sent);
            case RUN_RECEIVED:
                return context.getString(R.string.states_run_received);
            case RUN_ACCEPTED:
                return context.getString(R.string.states_run_accepted);
            case RUN_REJECTED:
                return context.getString(R.string.states_run_rejected);
            case RUN_STARTED:
                return context.getString(R.string.states_run_started);
            case RUN_IN_PROGRESS:
                return context.getString(R.string.states_run_in_progress);
            case RUN_FINISHED:
                return context.getString(R.string.states_run_finished);
            case RUN_COMPLETED:
                return context.getString(R.string.states_run_completed);
            case RUN_CANCELLED:
                return context.getString(R.string.states_run_cancelled);
            case RUN_ABORTED:
                return context.getString(R.string.states_run_aborted);
            case RUN_LATE15:
                return context.getString(R.string.states_run_late15);
            case RUN_LATE30:
                return context.getString(R.string.states_run_late30);
            case RUN_LATE60:
                return context.getString(R.string.states_run_late60);
            case RUN_LOCATION_REACHED:
                return context.getString(R.string.states_run_location_reached);
            // stop states
            case STOP_IDLE:
                return context.getString(R.string.states_stop_not_started);
            case STOP_RUN_ACCEPTED:
                return context.getString(R.string.states_stop_accepted);
            case STOP_RUN_STARTED:
                return context.getString(R.string.states_stop_started);
            case STOP_ON_ROUTE:
                return context.getString(R.string.states_stop_on_route);
            case STOP_ARRIVED:
                return context.getString(R.string.states_stop_arrived);
            case STOP_ARRIVE5:
                return context.getString(R.string.states_stop_arrive5);
            case STOP_ARRIVE10:
                return context.getString(R.string.states_stop_arrive10);
            case STOP_DROP5:
                return context.getString(R.string.states_stop_drop5);
            case STOP_DROP10:
                return context.getString(R.string.states_stop_drop10);
            case STOP_LATE15:
                return context.getString(R.string.states_stop_late15);
            case STOP_LATE30:
                return context.getString(R.string.states_stop_late30);
            case STOP_LATE60:
                return context.getString(R.string.states_stop_late60);
            case STOP_LATE_A_LOT:
                return context.getString(R.string.states_stop_late_a_lot);
            case STOP_RUN_FINISHED:
                return context.getString(R.string.states_stop_finished);
            case STOP_COMPLETED:
                return context.getString(R.string.states_stop_completed);
            case STOP_FAIL:
                return context.getString(R.string.states_stop_fail);
            case STOP_ABORTED:
                return context.getString(R.string.states_stop_aborted);
            case STOP_SUSPENDED:
                return context.getString(R.string.states_stop_suspended);
        }
        return null;
    }
}
