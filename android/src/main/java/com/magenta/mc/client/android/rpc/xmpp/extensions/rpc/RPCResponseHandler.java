package com.magenta.mc.client.android.rpc.xmpp.extensions.rpc;

public interface RPCResponseHandler {

    boolean handleError(String id);
}