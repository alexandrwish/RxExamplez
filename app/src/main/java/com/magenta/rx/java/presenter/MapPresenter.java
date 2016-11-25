package com.magenta.rx.java.presenter;

import com.magenta.rx.kotlin.event.CleanEvent;
import com.magenta.rx.kotlin.event.DrawMapEvent;
import com.magenta.rx.kotlin.loader.MapLoader;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class MapPresenter {

    private final MapLoader loader;

    @Inject
    public MapPresenter(MapLoader loader) {
        this.loader = loader;
    }

    public void draw() {
        EventBus.getDefault().postSticky(new DrawMapEvent(loader.getX(), loader.getY()));
    }

    public void clear() {
        EventBus.getDefault().postSticky(new CleanEvent());
    }
}