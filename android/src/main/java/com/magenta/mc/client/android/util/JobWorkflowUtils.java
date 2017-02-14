package com.magenta.mc.client.android.util;

import android.content.Context;
import android.content.Intent;

import com.magenta.mc.client.android.entity.AbstractStop;
import com.magenta.mc.client.android.entity.TaskState;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.service.storage.entity.Job;
import com.magenta.mc.client.android.service.storage.entity.Stop;
import com.magenta.mc.client.android.ui.activity.StartActivity;

import java.util.HashMap;
import java.util.List;

public class JobWorkflowUtils {

    private static final WorkflowStatusAuto AUTO_STATUSES = new WorkflowStatusAuto();

    static {
        AUTO_STATUSES.addEntityStatus(WorkflowStatusAuto.Entity.KIND_JOB, new WorkflowStatusAuto.Status(TaskState.RUN_ABORTED, new WorkflowStatusAuto.Expression(WorkflowStatusAuto.Expression.GROUP_STOPS, TaskState.STOP_ABORTED)));
        AUTO_STATUSES.addEntityStatus(WorkflowStatusAuto.Entity.KIND_JOB, new WorkflowStatusAuto.Status(TaskState.RUN_ABORTED, new WorkflowStatusAuto.Expression(WorkflowStatusAuto.Expression.GROUP_STOPS, TaskState.STOP_FAIL)));
        AUTO_STATUSES.addEntityStatus(WorkflowStatusAuto.Entity.KIND_JOB, new WorkflowStatusAuto.Status(TaskState.RUN_COMPLETED, new WorkflowStatusAuto.Expression(WorkflowStatusAuto.Expression.GROUP_STOPS, TaskState.STOP_COMPLETED)));
    }

    /**
     * Find next auto state for job
     *
     * @param kind kind of entity
     * @param job  job for checking
     * @param stop stop for checking
     * @return new state or {@link Integer#MIN_VALUE} if state not founded
     */
    private static int nextStatus(final byte kind, final Job job, final Stop stop) {
        final HashMap entities = AUTO_STATUSES.getEntities();
        final WorkflowStatusAuto.Entity entity = (WorkflowStatusAuto.Entity) entities.get(kind);
        return entity == null ? Integer.MIN_VALUE : nextEntityStatus(entity, kind, job, stop);
    }

    public static int nextStatus(final byte kind, final Job job) {
        return nextStatus(kind, job, null);
    }

    private static int nextEntityStatus(final WorkflowStatusAuto.Entity entity, final byte kind, final Job job, final Stop stop) {
        for (Object o : entity.getStatuses()) {
            final WorkflowStatusAuto.Status status = (WorkflowStatusAuto.Status) o;
            if (checkStatusExpression(status, kind, job, stop)) {
                return status.getValue();
            }
        }
        return Integer.MIN_VALUE;
    }

    private static boolean checkStatusExpression(final WorkflowStatusAuto.Status status, final byte kind, final Job job, final Stop stop) {
        return checkExpression(status.getExpression(), kind, job, stop);
    }

    private static boolean checkExpression(final WorkflowStatusAuto.Expression expression, final byte kind, final Job job, final Stop stop) {
        if (expression.getGroup() > WorkflowStatusAuto.Expression.GROUP_NONE) {
            switch (expression.getGroup()) {
                case WorkflowStatusAuto.Expression.GROUP_STOPS:
                    return checkExpressionGroupStops(expression.getState(), job);
            }
        } else {
            switch (kind) {
                case WorkflowStatusAuto.Entity.KIND_JOB:
                    return job != null && job.getState() == expression.getState();
                case WorkflowStatusAuto.Entity.KIND_STOP:
                    return stop != null && stop.getState() == expression.getState();
            }
        }
        return false;
    }

    private static boolean checkExpressionGroupStops(final int state, final Job job) {
        if (job == null) {
            return false;
        }
        for (Object o : job.getStops()) {
            final Stop stop = (Stop) o;
            if (stop.getState() != state) {
                return false;
            }
        }
        return true;
    }

    public static void openNextActivity(AbstractStop currentStop, Job currentJob, Context context) {
        if (currentStop.isCompleted()) return;
        String state = currentStop.getStateString();
        Intent intent = (TaskState.STR_STOP_IDLE.equalsIgnoreCase(state)
                || TaskState.STR_STOP_RUN_ACCEPTED.equalsIgnoreCase(state)
                || TaskState.STR_STOP_RUN_STARTED.equalsIgnoreCase(state)
                || TaskState.STR_STOP_SUSPENDED.equalsIgnoreCase(state))
                ? new Intent(context, ServicesRegistry.getWorkflowService().getStartActivity())
                : new Intent(context, ServicesRegistry.getWorkflowService().getArrivedActivity());
        intent.putExtra(IntentAttributes.JOB_ID, currentJob.getId()).putExtra(IntentAttributes.STOP_ID, currentStop.getReferenceId());
        if (!((MxSettings) Setup.get().getSettings()).isAllowToPassStopsInArbitraryOrder()) {
            boolean allPreviousStopsCompleted = true;
            List<AbstractStop> stops = currentJob.getStops();
            for (AbstractStop stop : stops) {
                if (stop.getStopName().equals(currentStop.getStopName())) {
                    break;
                }
                int stopState = stop.getState();
                if (stopState != TaskState.STOP_RUN_FINISHED
                        && stopState != TaskState.STOP_COMPLETED
                        && stopState != TaskState.STOP_FAIL
                        && stopState != TaskState.STOP_ABORTED
                        && stopState != TaskState.STOP_SUSPENDED) {
                    allPreviousStopsCompleted = false;
                    break;
                }
            }
            if (!allPreviousStopsCompleted) {
                intent.putExtra(StartActivity.DISABLE_START_BUTTON, true);
            }
        }
        context.startActivity(intent);
    }
}