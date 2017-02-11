package com.magenta.mc.client.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.magenta.mc.client.android.DistributionApplication;
import com.magenta.mc.client.android.handler.HDUpdateHandler;
import com.magenta.mc.client.android.handler.UpdateHandler;
import com.magenta.mc.client.android.service.WorkflowService;
import com.magenta.mc.client.android.ui.activity.ActivityDecorator;
import com.magenta.mc.client.android.ui.activity.ArriveMapActivity;
import com.magenta.mc.client.android.ui.activity.HDActivityDecorator;
import com.magenta.mc.client.android.ui.activity.JobActivity;
import com.magenta.mc.client.android.ui.activity.JobsActivity;
import com.magenta.mc.client.android.ui.activity.StartActivity;
import com.magenta.mc.client.android.ui.activity.WorkflowActivity;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;
import com.magenta.mc.client.android.ui.activity.common.SettingsActivity;

public class WorkflowServiceImpl implements WorkflowService {

    private static final UpdateHandler updateHandler = new HDUpdateHandler();
    private final Context context;

    public WorkflowServiceImpl(Context context) {
        this.context = context;
    }

    public void logout(Bundle... bundles) {
        Intent intent = new Intent(DistributionApplication.getContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundles != null && bundles.length > 0) {
            intent.putExtras(bundles[0]);
        }
        DistributionApplication.getContext().startActivity(intent);
    }

    public void showNextActivity(int flags) {
        context.startActivity(new Intent(context, getFirstActivity()).putExtra("after-login", true).addFlags(flags));
    }

    public Class<? extends WorkflowActivity> getFirstActivity() {
        return JobsActivity.class;
    }

    public void showSettings(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, SettingsActivity.class), requestCode);
    }

    public Class<? extends WorkflowActivity> getStartActivity() {
        return StartActivity.class;
    }

    public Class<? extends WorkflowActivity> getJobActivity() {
        return JobActivity.class;
    }

    public Class<? extends WorkflowActivity> getArrivedActivity() {
        return ArriveMapActivity.class;
    }

    public ActivityDecorator getDecorator(final Context context) {
        return new HDActivityDecorator(context);
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }
}