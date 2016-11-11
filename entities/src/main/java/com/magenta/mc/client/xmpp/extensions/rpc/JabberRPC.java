package com.magenta.mc.client.xmpp.extensions.rpc;

import com.magenta.mc.client.client.XMPPClient;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xml.XMLBlockListener;
import com.magenta.mc.client.xml.XMLDataBlock;
import com.magenta.mc.client.xmpp.datablocks.Iq;

/**
 * Created 03.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class JabberRPC implements XMLBlockListener {
    private static JabberRPC instance;
    private RPCQueryListener listener;
    private RPCResponseHandler handler;

    private JabberRPC() {

    }

    public static JabberRPC getInstance() {
        if (instance == null) {
            instance = new JabberRPC();
        }
        return instance;
    }

    public static Object[] decomposeId(String id) {
        final int separatorIndex = id.indexOf(":");
        Object[] result = new Object[2];
        result[0] = id.substring(0, separatorIndex);
        result[1] = new Long(id.substring(separatorIndex + 1, id.length()));
        return result;
    }

    public RPCQueryListener getListener() {
        return listener;
    }

    public void setListener(RPCQueryListener listener) {
        this.listener = listener;
    }

    public RPCResponseHandler getHandler() {
        return handler;
    }

    public void setHandler(RPCResponseHandler handler) {
        this.handler = handler;
    }

    public int blockArrived(XMLDataBlock iq) {
        if (!(iq instanceof Iq)) {
            return BLOCK_REJECTED;
        }

        XMLDataBlock query = iq.findNamespace("query", "jabber:iq:rpc");
        if (query == null) {
            return BLOCK_REJECTED;
        }

        String from = iq.getAttribute("from"); // todo: check server name?

        String id = iq.getAttribute("id"); // todo: use this to route responses and errors

        String iqType = iq.getAttribute("type");

        if (iqType.equals("set")) {
            final MethodCall call = MethodCall.create(query.getChildBlock("methodCall"));

            try {
                invoke(call);
            } catch (Exception e) {
                // Sent an RPC-error
                XMLDataBlock iqError = new Iq(from != null ? from : Setup.get().getSettings().getServerComponentJid(), Iq.TYPE_ERROR, id);
                iqError.setAttribute("from", Setup.get().getSettings().getJid());
                iqError.addChildNs("query", "jabber:iq:rpc");
                XMPPClient.getInstance().send(iqError);
                return BLOCK_PROCESSED;
            }

            // send an RPC-result
            XMLDataBlock iqResult = new Iq(from, Iq.TYPE_RESULT, id);
            iqResult.setAttribute("from", Setup.get().getSettings().getJid());
            iqResult.addChildNs("query", "jabber:iq:rpc");
            XMPPClient.getInstance().send(iqResult);
            return BLOCK_PROCESSED;
        } else if (iqType.equals("result")) {
            MCLoggerFactory.getLogger(getClass()).debug("result returned: " + iq.getAttribute("id"));

            final Object[] methodNameAndId = decomposeId(id);
            final String methodName = (String) methodNameAndId[0];
            final Long responseId = (Long) methodNameAndId[1];
            final MethodResponse methodResponse = MethodResponse.create(query.getChildBlock("methodResponse"), methodName, responseId);
            methodResponse.invoke(handler);
        } else if (iqType.equals("error")) {
            handler.handleError(id);
        }

        return BLOCK_REJECTED;
    }

    // invoke incoming RPC-call on listeners

    private void invoke(MethodCall call) {
        call.invoke(listener);
    }

    // call remote method on server
    public void call(String methodName, Object[] args, Long iqId) {
        call(Setup.get().getSettings().getServerComponentJid(), methodName, args, iqId);
    }

    public void call(String destinationJid, String methodName, Object[] args, Long iqId) {
        if (XMPPClient.getInstance().isLoggedIn()) {
            MCLoggerFactory.getLogger(getClass()).debug("RPC call: " + destinationJid + "." + methodName + "(" + args + ")");
            final MethodCall call = MethodCall.create(methodName, args);
            XMPPClient.getInstance().send(constructIQ(destinationJid, call, iqId));
        } else {
            MCLoggerFactory.getLogger(getClass()).warn("Can't RPC call: " + destinationJid + "." + methodName + "(" + args + ")");
        }
    }

    private String generateIqId(MethodCall call, Long id) {
        if (id == null) {
            id = new Long(Math.round(Math.random() * Long.MAX_VALUE));
        }
        return call.getMethodName() + ":" + id;
    }

    private XMLDataBlock constructIQ(String to, MethodCall call, Long id) {
        String iqId = generateIqId(call, id);
        final Iq iq = new Iq(to, Iq.TYPE_SET, iqId);
        iq.setFrom(Setup.get().getSettings().getJid());
        final XMLDataBlock query = iq.addChildNs("query", "jabber:iq:rpc");
        query.addChild(call.getDataBlock());

        return iq;
    }
}
