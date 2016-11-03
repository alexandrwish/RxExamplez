package com.magenta.maxunits.mobile.events;


import com.magenta.maxunits.mobile.service.listeners.BroadcastEvent;

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
