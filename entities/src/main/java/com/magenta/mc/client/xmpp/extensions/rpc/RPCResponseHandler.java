package com.magenta.mc.client.xmpp.extensions.rpc;

/**
 * Created 27.04.2010
 *
 * @author Konstantin Pestrikov
 */
public interface RPCResponseHandler {
    boolean handleError(String id);
}
