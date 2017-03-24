package com.magenta.mc.client.android.listener;

import java.util.Set;

public interface BroadcastEventsListener<E extends BroadcastEvent<T>, T> {

    String getId();

    Set<T> getFilter();

    void onEvent(E event);
}