package com.magenta.maxunits.mobile.dlib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.magenta.maxunits.mobile.dlib.MxApplication;
import com.magenta.maxunits.mobile.dlib.activity.ActivityDecorator;
import com.magenta.maxunits.mobile.dlib.activity.ArriveMapActivity;
import com.magenta.maxunits.mobile.dlib.activity.HDActivityDecorator;
import com.magenta.maxunits.mobile.dlib.activity.JobActivity;
import com.magenta.maxunits.mobile.dlib.activity.JobsActivity;
import com.magenta.maxunits.mobile.dlib.activity.StartActivity;
import com.magenta.maxunits.mobile.dlib.activity.WorkflowActivity;
import com.magenta.maxunits.mobile.dlib.activity.common.LoginActivity;
import com.magenta.maxunits.mobile.dlib.activity.common.SettingsActivity;
import com.magenta.maxunits.mobile.dlib.handler.HDUpdateHandler;
import com.magenta.maxunits.mobile.dlib.handler.UpdateHandler;
import com.magenta.maxunits.mobile.dlib.service.WorkflowService;

public class WorkflowServiceImpl implements WorkflowService {

    private static final UpdateHandler updateHandler = new HDUpdateHandler();
    private final Context context;

    public WorkflowServiceImpl(Context context) {
        this.context = context;
    }

    public void logout(Bundle... bundles) {
        Intent intent = new Intent(MxApplication.getContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundles != null && bundles.length > 0) {
            intent.putExtras(bundles[0]);
        }
        MxApplication.getContext().startActivity(intent);
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