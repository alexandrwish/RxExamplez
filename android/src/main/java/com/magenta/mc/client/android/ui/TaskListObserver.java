package com.magenta.mc.client.android.ui;

/**
 * @autor Petr Popov
 * Created 19.04.12 18:16
 */
public interface TaskListObserver<T> {

    void onUpdate(T[] tasks);

    String getObserverName();

}
