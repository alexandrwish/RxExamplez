package com.magenta.mc.client.android.record;

import java.io.Serializable;

public class UpdateRunEvent implements Serializable {

    private final String reference;
    private final Long timestamp;

    public UpdateRunEvent(Long timestamp, String reference) {
        this.timestamp = timestamp;
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}