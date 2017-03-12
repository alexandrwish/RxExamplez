package com.magenta.mc.client.android.service;

import android.content.Context;
import android.os.Bundle;

import com.magenta.mc.client.android.ui.activity.ActivityDecorator;

public interface WorkflowService {

    void logout(Bundle... bundles);

    ActivityDecorator getDecorator(Context context);
}