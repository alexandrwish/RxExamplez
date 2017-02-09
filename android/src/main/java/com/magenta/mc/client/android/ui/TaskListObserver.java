package com.magenta.mc.client.android.ui;

public interface TaskListObserver<T> {

    void onUpdate(T[] tasks);

    String getObserverName();
}