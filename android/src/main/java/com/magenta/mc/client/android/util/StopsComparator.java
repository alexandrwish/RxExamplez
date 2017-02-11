package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.service.storage.entity.Stop;

import java.util.Comparator;

public class StopsComparator implements Comparator<Stop> {

    private static final StopsComparator INSTANCE = new StopsComparator();

    public static StopsComparator getInstance() {
        return INSTANCE;
    }

    public int compare(Stop stop, Stop stop2) {
        return stop.getDate() != null ? stop.getDate().compareTo(stop2.getDate()) : (stop2.getDate() == null ? 0 : -1);
    }
}