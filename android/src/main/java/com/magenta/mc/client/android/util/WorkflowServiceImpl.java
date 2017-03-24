package com.magenta.mc.client.android.util;

import android.content.Context;

import com.magenta.mc.client.android.service.WorkflowService;
import com.magenta.mc.client.android.ui.activity.ActivityDecorator;

public class WorkflowServiceImpl implements WorkflowService {

    public ActivityDecorator getDecorator(final Context context) {
        return new ActivityDecorator(context);
    }
}