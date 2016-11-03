package com.magenta.maxunits.mobile.service.listeners;


import java.util.Set;

/**
 * @author Sergey Grachev
 */
public interface BroadcastEventsListener<E extends BroadcastEvent<T>, T> {
    String getId();

    Set<T> getFilter();

    void onEvent(E event);
}
