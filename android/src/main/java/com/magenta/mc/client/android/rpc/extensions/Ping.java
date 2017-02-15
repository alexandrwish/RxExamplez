package com.magenta.mc.client.android.rpc.extensions;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;

public class Ping implements XMLBlockListener {

    public Ping() {
    }

    public int blockArrived(XMLDataBlock data) {
        if (!(data instanceof Iq)) {
            return BLOCK_REJECTED;
        }

        String from = data.getAttribute("from");
        String id = data.getAttribute("id");
        String type = data.getTypeAttribute();

        if (type.equals("result") || type.equals("error")) {
            if (!id.equals("ping")) {
                return BLOCK_REJECTED;
            }
            if (type.equals("result")) {
                XMPPClient.getInstance().getXmppStream().pingResponce();
            } else {
                MCLoggerFactory.getLogger(getClass()).warn("Warning: Ping error received, considering as invalid ping responce");
            }
            return BLOCK_PROCESSED;
        }
        if (type.equals("get")) {
            if (data.findNamespace("ping", "urn:xmpp:ping") == null) {
                return BLOCK_REJECTED;
            }
            // xep-0199 ping
            Iq pong = new Iq(from, Iq.TYPE_RESULT, data.getAttribute("id"));
            XMPPClient.getInstance().send(pong);
            return BLOCK_PROCESSED;
        }
        return BLOCK_REJECTED;
    }
}