package com.magenta.rx.java.event;

public class ReceivedLocationEvent {

    private final double lat;
    private final double lon;
    private final long time;

    public ReceivedLocationEvent(double lat, double lon, long time) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getTime() {
        return time;
    }
}