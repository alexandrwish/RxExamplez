package com.magenta.mc.client.android.service;

import com.magenta.mc.client.android.listener.BroadcastEvent;
import com.magenta.mc.client.android.listener.BroadcastEventsListener;

public interface CoreService {

    void stopSelf();

    void registerListener(BroadcastEventsListener listener);

    void removeListener(BroadcastEventsListener listener);

    void removeListener(String id);

    void notifyListeners(BroadcastEvent event);
}