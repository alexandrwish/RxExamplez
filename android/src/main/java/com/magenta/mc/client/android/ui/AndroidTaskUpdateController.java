package com.magenta.mc.client.android.ui;

import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.ui.TaskUpdateController;

import java.util.HashMap;
import java.util.Map;

/**
 * @autor Petr Popov
 * Created 23.05.12 18:07
 */
public abstract class AndroidTaskUpdateController<TASK> extends TaskUpdateController {

    private Map<String, TaskListObserver<TASK>> observers = new HashMap<String, TaskListObserver<TASK>>();

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

    @Override
    protected void switchToNewScreen() {
        MCLoggerFactory.getLogger(getClass()).info("Switch to new screen");
    }

    @Override
    protected void playIncomingMessageSound() {
        MCLoggerFactory.getLogger(getClass()).info("Play incoming message sound");
    }
}