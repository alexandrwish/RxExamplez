package com.magenta.maxunits.mobile.service;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.magenta.maxunits.mobile.activity.ActivityDecorator;
import com.magenta.maxunits.mobile.activity.WorkflowActivity;
import com.magenta.maxunits.mobile.handler.UpdateHandler;

public interface WorkflowService {

    void logout(Bundle... bundles);

    void showNextActivity();

    void showNextActivity(int flags);

    void showSettings(Activity activity, int requestCode);

    Class<? extends WorkflowActivity> getFirstActivity();

    Class<? extends WorkflowActivity> getStartActivity();

    Class<? extends WorkflowActivity> getJobActivity();

    Class<? extends WorkflowActivity> getArrivedActivity();

    ActivityDecorator getDecorator(Context context);

    UpdateHandler getUpdateHandler();
}