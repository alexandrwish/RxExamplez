package com.magenta.rx.java.presenter;

import com.magenta.rx.java.event.CleanMapEvent;
import com.magenta.rx.java.event.DrawMapEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

public class MapPresenter {

    public void draw() {
        Random random = new Random();
        int max = 1800, min = -1800;
        EventBus.getDefault().postSticky(new DrawMapEvent((random.nextInt(max - min + 1) + min) / 10.0, (random.nextInt(max - min + 1) + min) / 10.0));
    }

    public void clear() {
        EventBus.getDefault().postSticky(new CleanMapEvent());
    }
}