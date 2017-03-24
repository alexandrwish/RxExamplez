package com.magenta.mc.client.android.events;

import com.magenta.mc.client.android.listener.BroadcastEvent;

public class InstallUpdateEvent extends BroadcastEvent<String> {

    private final String path;

    public InstallUpdateEvent(String type, String path) {
        super(type);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}