package com.magenta.mc.client.android.mc.xmpp.extensions.rpc;

public interface RPCResponseHandler {

    boolean handleError(String id);
}