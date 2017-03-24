package com.magenta.mc.client.android.service;

import android.content.Context;

import com.magenta.mc.client.android.ui.activity.ActivityDecorator;

public interface WorkflowService {

    ActivityDecorator getDecorator(Context context);
}