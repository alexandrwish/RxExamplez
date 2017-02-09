package com.magenta.maxunits.mobile.dlib.service.storage.entity;

import android.os.Message;

import com.magenta.maxunits.mobile.dlib.DistributionApplication;
import com.magenta.maxunits.mobile.dlib.db.dao.DistributionDAO;
import com.magenta.maxunits.mobile.dlib.rpc.DistributionRPCOut;
import com.magenta.maxunits.mobile.entity.AbstractJobStatus;
import com.magenta.maxunits.mobile.dlib.entity.JobStatusEntity;
import com.magenta.maxunits.mobile.dlib.handler.UpdateHandler;
import com.magenta.maxunits.mobile.dlib.service.ServicesRegistry;
import com.magenta.mc.client.setup.Setup;

import java.sql.SQLException;

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
        DistributionRPCOut.jobStates(Long.parseLong(getId()), Setup.get().getSettings().getUserId(), getJobReferenceId(), getJobStatus(), valuesToString());
    }

    public boolean canSent() {
        try {
            return DistributionDAO.getInstance(DistributionApplication.getContext()).hasLock(this);
        } catch (SQLException e) {
            return true;
        }
    }
}