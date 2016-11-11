package com.magenta.mc.client.ui;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 28.06.11
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class TaskUpdateAction {
    public static final int NEW_JOB = 0;
    public static final int JOB_CANCELLED = 1;
    public static final int JOB_UPDATED = 2;
    public static final int JOB_COMPLETED = 3;
    public static final int JOB_FAILED = 4;

    public int action;
    public String jobRef;

    public TaskUpdateAction(String jobRef, int action) {
        this.jobRef = jobRef;
        this.action = action;
    }
}
