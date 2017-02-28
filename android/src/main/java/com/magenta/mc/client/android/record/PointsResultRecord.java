package com.magenta.mc.client.android.record;

import java.util.List;

public class PointsResultRecord extends ResultRecord {

    private List<PointRecord> points;

    public List<PointRecord> getPoints() {
        return points;
    }

    public void setPoints(List<PointRecord> points) {
        this.points = points;
    }

    public String toString() {
        return "PointsResultRecord{" +
                "points=" + points +
                '}';
    }
}