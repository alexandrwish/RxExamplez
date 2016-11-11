package com.magenta.mc.client.client;

import com.magenta.mc.client.xmpp.datablocks.Presence;

/**
 * User: stukov
 * Date: 27.08.2010
 * Time: 18:40:38
 */
public interface StatusExtender {
    void extend(Presence presence);
}
