package com.magenta.mc.client.android.entity;

public interface JobEntity {

    String getId();

    void processSetState(int state);
}