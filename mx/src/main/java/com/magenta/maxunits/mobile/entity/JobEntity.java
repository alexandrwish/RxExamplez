package com.magenta.maxunits.mobile.entity;

/**
 * @author Sergey Grachev
 */
public interface JobEntity {
    String getId();

    void processSetState(int state);
}
