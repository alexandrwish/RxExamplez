package com.magenta.mc.client.android.service;

import android.util.Pair;

import com.magenta.hdmate.mx.model.JobRecord;
import com.magenta.mc.client.android.entity.JobEntity;
import com.magenta.mc.client.android.entity.JobStatusEntity;

import java.util.List;
import java.util.Map;

public interface DataController<J extends JobEntity, S extends JobStatusEntity, STOP> {

    void updateJob(J job, boolean silent);

    void updateJob(J job);

    void reloadJobs(List<J> jobs);

    void addJob(J job);

    void cancelJob(String referenceId);

    void clear();

    J findJob(String refId);

    Pair<J, STOP> find(String jobId, String stopId);

    void init();

    S saveJobStatus(J job, Map<String, String> parameters);

    void save(J job);

    List<J> loadCurrentJobs();

    void checkCancelledAndCompletedJobs();

    void reloadNewJobs(List<JobRecord> records);
}