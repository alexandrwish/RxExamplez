package com.magenta.rx.java.presenter;

import com.magenta.rx.kotlin.event.CleanEvent;
import com.magenta.rx.kotlin.event.DrawMapEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import javax.inject.Inject;

public class MapPresenter {

    @Inject
    public MapPresenter() {
    }

    public void draw() {
        Random random = new Random();
        int max = 900, min = -900;
        EventBus.getDefault().postSticky(new DrawMapEvent((random.nextInt(max - min + 1) + min) / 10.0, (random.nextInt(max - min + 1) + min) / 10.0));
    }

    public void clear() {
        EventBus.getDefault().postSticky(new CleanEvent());
    }
}