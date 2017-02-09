package com.magenta.maxunits.mobile.dlib.service.storage;

import com.magenta.maxunits.mobile.dlib.entity.JobEntity;
import com.magenta.maxunits.mobile.dlib.service.ServicesRegistry;

import java.util.HashMap;
import java.util.Map;

public class StateController {

    private final static Map<String, String> currentJobActivity = new HashMap<>();
    private static String currentJob;

    @SuppressWarnings("unchecked")
    public static <T extends JobEntity> T getCurrentJob() {
        return (T) ServicesRegistry.getDataController().findJob(currentJob);
    }

    public static void setCurrentJob(JobEntity jobEntity) {
        currentJob = jobEntity.getId();
    }

    public static void setCurrentJobActivity(final String name) {
        currentJobActivity.put(currentJob, name);
    }

    public static String getCurrentJobActivity(final String jobId) {
        return currentJobActivity.get(jobId);
    }

    public static void cleanCurrentJob() {
        currentJob = null;
    }
}