package com.magenta.maxunits.mobile.dlib.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "status_sender_lock")
public class StatusSenderLock extends AbstractEntity {

    @DatabaseField(columnName = "job_id", index = true)
    String jobId;
    @DatabaseField(columnName = "job_ref", index = true)
    String jobReferenceId;
    @DatabaseField(columnName = "job_status", index = true)
    String jobStatus;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobReferenceId() {
        return jobReferenceId;
    }

    public void setJobReferenceId(String jobReferenceId) {
        this.jobReferenceId = jobReferenceId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Object toRecord() {
        return null;
    }
}