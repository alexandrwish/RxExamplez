package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.entity.AbstractStop;

import java.util.Comparator;

public class StopsComparator implements Comparator<AbstractStop> {

    private static final StopsComparator INSTANCE = new StopsComparator();

    public static StopsComparator getInstance() {
        return INSTANCE;
    }

    public int compare(AbstractStop stop1, AbstractStop stop2) {
        return stop1.getDate() != null ? stop1.getDate().compareTo(stop2.getDate()) : (stop2.getDate() == null ? 0 : -1);
    }
}