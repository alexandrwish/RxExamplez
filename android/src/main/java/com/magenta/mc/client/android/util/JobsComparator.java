package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.entity.Job;

import java.util.Comparator;

public class JobsComparator implements Comparator<Job> {

    private final static JobsComparator INSTANCE = new JobsComparator();

    private JobsComparator() {
    }

    public static JobsComparator getInstance() {
        return INSTANCE;
    }

    public int compare(Job j1, Job j2) {
        return j1.getDate() != null && j2.getDate() != null ? j1.getDate().compareTo(j2.getDate()) : 0;
    }
}