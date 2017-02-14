package com.magenta.mc.client.android.ui;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.ui.TaskUpdateController;

import java.util.HashMap;
import java.util.Map;

public abstract class AndroidTaskUpdateController<TASK> extends TaskUpdateController {

    private Map<String, TaskListObserver<TASK>> observers = new HashMap<>();

    protected Map<String, TaskListObserver<TASK>> getObservers() {
        return observers;
    }

    public void registerJobListObserver(TaskListObserver<TASK> observer) {
        MCLoggerFactory.getLogger(getClass()).debug("Register job list observer for " + observer.getObserverName());
        observers.remove(observer.getObserverName());
        observers.put(observer.getObserverName(), observer);
    }

    public void unregisterJobListObserver(TaskListObserver<TASK> observer) {
        MCLoggerFactory.getLogger(getClass()).debug("Unregister job list observer for " + observer.getObserverName());
        observers.remove(observer.getObserverName());
    }

    protected void switchToNewScreen() {
        MCLoggerFactory.getLogger(getClass()).info("Switch to new screen");
    }

    protected void playIncomingMessageSound() {
        MCLoggerFactory.getLogger(getClass()).info("Play incoming message sound");
    }
}