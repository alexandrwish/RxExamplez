package com.magenta.mc.client.android.rpc.xmpp.datablocks;

import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.util.Vector;

public class Iq extends XMLDataBlock {

    public final static int TYPE_SET = 0;
    public final static int TYPE_GET = 1;
    public final static int TYPE_RESULT = 2;
    public final static int TYPE_ERROR = 3;

    public Iq(XMLDataBlock _parent, Vector _attributes) {
        super(_parent, _attributes);
    }

    public Iq(String to, int typeSet, String id) {
        super();
        setAttribute("to", to);
        String type;
        switch (typeSet) {
            case TYPE_SET:
                type = "set";
                break;
            case TYPE_GET:
                type = "get";
                break;
            case TYPE_ERROR:
                type = "error";
                break;
            default:
                type = "result";
        }
        setAttribute("type", type);
        setAttribute("id", id);
    }

    public void setFrom(String jid) {
        setAttribute("from", jid);
    }

    public String getTagName() {
        return "iq";
    }
}