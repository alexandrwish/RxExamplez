package com.magenta.mc.client.android.rpc.extensions;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.locale.SR;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;

public class IqVersionReply implements XMLBlockListener {

    private final static String TOPFIELDS[] = {"name", "version", "os"};

    public IqVersionReply() {
    }

    public static XMLDataBlock query(String to) {
        XMLDataBlock result = new Iq(to, Iq.TYPE_GET, "getver");
        result.addChildNs("query", "jabber:iq:version");
        return result;
    }

    public int blockArrived(XMLDataBlock data) {
        if (!(data instanceof Iq)) {
            return BLOCK_REJECTED;
        }
        String type = data.getAttribute("type");
        if (type.equals("get")) {
            XMLDataBlock query = data.findNamespace("query", "jabber:iq:version");
            if (query == null) {
                return BLOCK_REJECTED;
            }
            Iq reply = new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
            //XMLDataBlock query=reply.addChildNs("query", "jabber:iq:version"); reuse existing request
            reply.addChild(query);
            query.addChild("name", Setup.get().getSettings().getAppName());
            query.addChild("version", Setup.get().getSettings().getAppVersion());
            XMPPClient.getInstance().send(reply);
            return XMLBlockListener.BLOCK_PROCESSED;
        }
        if (data.getAttribute("id").equals("getver")) {
            String body = null;
            switch (type) {
                case "error":
                    body = SR.MS_NO_VERSION_AVAILABLE;
                    break;
                case "result":
                    XMLDataBlock vc = data.getChildBlock("query");
                    if (vc != null) {
                        body = dispatchVersion(vc);
                    }
                    break;
                default:
                    return BLOCK_REJECTED;
            }
            if (body != null) {
                return XMLBlockListener.BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }

    private String dispatchVersion(XMLDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:version")) {
            return "unknown version namespace";
        }
        StringBuilder vc = new StringBuilder();
        for (String topField : TOPFIELDS) {
            String field = data.getChildBlockText(topField);
            if (field.length() > 0) {
                vc.append(topField).append((char) 0xa0).append(field).append('\n');
            }
        }
        return vc.toString();
    }
}