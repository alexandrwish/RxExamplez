package com.magenta.mc.client.android.service.storage.entity;

import com.magenta.mc.client.android.entity.AbstractJobStatus;
import com.magenta.mc.client.android.entity.JobStatusEntity;

public class JobStatus extends AbstractJobStatus implements JobStatusEntity {

    public boolean send() {
//        DistributionRPCOut.jobStates(Long.parseLong(getId()), Setup.get().getSettings().getUserId(), getJobReferenceId(), getJobStatus(), getValues());
        return true;
    }
}