package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.rpc.xmpp.datablocks.Presence;

public interface StatusExtender {

    void extend(Presence presence);
}