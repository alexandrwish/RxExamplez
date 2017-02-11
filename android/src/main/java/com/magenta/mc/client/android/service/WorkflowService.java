package com.magenta.mc.client.android.service;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.magenta.mc.client.android.ui.activity.ActivityDecorator;
import com.magenta.mc.client.android.ui.activity.WorkflowActivity;
import com.magenta.mc.client.android.handler.UpdateHandler;

public interface WorkflowService {

    void logout(Bundle... bundles);

    void showNextActivity(int flags);

    void showSettings(Activity activity, int requestCode);

    Class<? extends WorkflowActivity> getFirstActivity();

    Class<? extends WorkflowActivity> getStartActivity();

    Class<? extends WorkflowActivity> getJobActivity();

    Class<? extends WorkflowActivity> getArrivedActivity();

    ActivityDecorator getDecorator(Context context);

    UpdateHandler getUpdateHandler();
}