package com.magenta.maxunits.mobile.service;

import com.magenta.maxunits.mobile.service.listeners.BroadcastEvent;
import com.magenta.maxunits.mobile.service.listeners.BroadcastEventsListener;

public interface CoreService {

    void stopSelf();

    void registerListener(BroadcastEventsListener listener);

    void removeListener(BroadcastEventsListener listener);

    void removeListener(String id);

    void notifyListeners(BroadcastEvent event);
}
