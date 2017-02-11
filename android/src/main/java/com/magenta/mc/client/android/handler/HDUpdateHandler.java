package com.magenta.mc.client.android.handler;

import android.os.Message;

import com.magenta.mc.client.android.service.storage.entity.JobStatus;
import com.magenta.mc.client.client.Login;

public class HDUpdateHandler extends UpdateHandler {

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