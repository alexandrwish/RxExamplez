package com.magenta.mc.client.android.workflow;

import android.view.View;

public abstract class WorkflowAction implements View.OnClickListener {

    public Runnable getFutureRunnable(final View v) {
        return new Runnable() {
            public void run() {

            }
        };
    }
}