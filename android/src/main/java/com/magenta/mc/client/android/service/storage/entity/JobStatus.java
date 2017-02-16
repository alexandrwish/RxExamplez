package com.magenta.mc.client.android.service.storage.entity;

import android.os.Message;

import com.magenta.mc.client.android.entity.AbstractJobStatus;
import com.magenta.mc.client.android.entity.JobStatusEntity;
import com.magenta.mc.client.android.handler.UpdateHandler;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.rpc.DistributionRPCOut;
import com.magenta.mc.client.android.service.ServicesRegistry;

public class JobStatus extends AbstractJobStatus implements JobStatusEntity {

    public static final int UPDATE_STATE = 191015;

    public boolean send() {
        final UpdateHandler handler = ServicesRegistry.getWorkflowService().getUpdateHandler();
        if (!handler.hasMessages(UPDATE_STATE, this)) {
            handler.sendMessageDelayed(Message.obtain(handler, UPDATE_STATE, this), 5000);
        }
        return true;
    }

    public void sent() {
        DistributionRPCOut.jobStates(Long.parseLong(getId()), Setup.get().getSettings().getUserId(), getJobReferenceId(), getJobStatus(), getValues());
    }
}