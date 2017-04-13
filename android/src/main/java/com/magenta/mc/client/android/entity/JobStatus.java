package com.magenta.mc.client.android.entity;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.http.HttpClient;

public class JobStatus extends AbstractJobStatus implements JobStatusEntity {

    public boolean send() {
        HttpClient.getInstance().sendState(Long.valueOf(getId()), Settings.get().getUserId(), getJobStatus(), getValues());
        return true;
    }
}