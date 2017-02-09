package com.magenta.maxunits.mobile.dlib.service.events;

import com.magenta.maxunits.mobile.dlib.service.listeners.BroadcastEvent;

import java.util.List;

public class AlertEvent extends BroadcastEvent<String> {

    protected final List<String> runs;
    protected final List<String> jobs;

    public AlertEvent(List<String> runs, List<String> jobs) {
        super(EventType.PERFORMER_ALERT);
        this.runs = runs;
        this.jobs = jobs;
    }

    public List<String> getRuns() {
        return runs;
    }

    public List<String> getJobs() {
        return jobs;
    }
}