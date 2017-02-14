package com.magenta.mc.client.android.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.magenta.mc.client.android.mc.client.Login;
import com.magenta.mc.client.android.service.storage.entity.JobStatus;

public class UpdateHandler extends Handler {

    public UpdateHandler() {
        super(Looper.getMainLooper());
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case JobStatus.UPDATE_STATE: {
                if (Login.isUserLoggedIn()) {
                    ((JobStatus) msg.obj).sent();
                }
            }
        }
    }
}