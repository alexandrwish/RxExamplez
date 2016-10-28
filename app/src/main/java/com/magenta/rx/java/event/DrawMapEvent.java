package com.magenta.rx.java.event;

public class DrawMapEvent {

    private final double x;
    private final double y;

    public DrawMapEvent(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}