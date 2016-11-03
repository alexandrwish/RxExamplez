package com.magenta.maxunits.mobile.dlib.entity;

import com.j256.ormlite.field.DatabaseField;

public abstract class JobStopEntity<T> extends AbstractEntity<T> {

    @DatabaseField(columnName = "stop", index = true)
    private String stop;
    @DatabaseField(columnName = "job", index = true)
    private String job;
    @DatabaseField(columnName = "mx_id")
    private String mxID;

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMxID() {
        return mxID;
    }

    public void setMxID(String mxID) {
        this.mxID = mxID;
    }
}