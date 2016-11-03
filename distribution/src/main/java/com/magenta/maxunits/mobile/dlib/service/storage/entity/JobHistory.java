package com.magenta.maxunits.mobile.dlib.service.storage.entity;

import com.magenta.maxunits.mobile.entity.AbstractJobHistory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: popov
 * Date: 16.12.11
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
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