package com.magenta.maxunits.mobile.dlib.utils;

import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;

import java.util.Comparator;

/**
 * @author Sergey Grachev
 */
public class StopsComparator implements Comparator<Stop> {

    private static final StopsComparator INSTANCE = new StopsComparator();

    public static StopsComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(Stop stop, Stop stop2) {
        return stop.getDate() != null ? stop.getDate().compareTo(stop2.getDate()) : (stop2.getDate() == null ? 0 : -1);
    }
}