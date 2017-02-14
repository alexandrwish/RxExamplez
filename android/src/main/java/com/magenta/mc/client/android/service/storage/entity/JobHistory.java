package com.magenta.mc.client.android.service.storage.entity;

import com.magenta.mc.client.android.entity.AbstractJobHistory;

import java.util.Date;

public class JobHistory extends AbstractJobHistory {

    public JobHistory() {
    }

    public JobHistory(Job job) {
        super(job);
    }

    public JobHistory(String referenceId, Date startDate, String service, String shortDescription, String waitReturn, int state) {
        super(referenceId, startDate, service, shortDescription, waitReturn, state);
    }
}