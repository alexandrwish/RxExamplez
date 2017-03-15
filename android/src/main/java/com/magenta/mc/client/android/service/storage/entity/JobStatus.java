package com.magenta.mc.client.android.service.storage.entity;

import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.entity.AbstractJobStatus;
import com.magenta.mc.client.android.entity.JobStatusEntity;
import com.magenta.mc.client.android.http.HttpClient;

public class JobStatus extends AbstractJobStatus implements JobStatusEntity {

    public boolean send() {
//        HttpClient.getInstance().sendState(Long.valueOf(getId()), Settings.get().getUserId(), getJobReferenceId(), getJobStatus(), getValues());
        return true;
    }
}