package com.magenta.maxunits.mobile.dlib.service.events;

import com.magenta.maxunits.mobile.dlib.service.listeners.BroadcastEvent;

public class JobEvent extends BroadcastEvent<String> {

    private final String referenceId;
    private final boolean requireAlert;

    public JobEvent(final String type, final String referenceId, final boolean requireAlert) {
        super(type);
        this.referenceId = referenceId;
        this.requireAlert = requireAlert;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public boolean isRequireAlert() {
        return requireAlert;
    }
}