package com.magenta.mc.client.android.rpc.xmpp.datablocks;

import com.magenta.mc.client.android.mc.util.Time;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.XmppError;

import java.util.Enumeration;
import java.util.Vector;

public class Message extends XMLDataBlock {

    public Message(String to, String message, String subject, boolean groupchat) {
        super();
        setAttribute("to", to);
        if (message != null) {
            setBodyText(message);
        }
        if (subject != null) {
            setSubject(subject);
        }
        setTypeAttribute((groupchat) ? "groupchat" : "chat");
    }

    public Message(String to) {
        super();
        setAttribute("to", to);
    }

    public Message() {
        this(null);
    }

    public Message(XMLDataBlock _parent, Vector _attributes) {
        super(_parent, _attributes);
    }

    public void setBodyText(String text) {
        addChild("body", text);
    }

    public String getSubject() {
        return getChildBlockText("subject");
    }

    public void setSubject(String text) {
        addChild("subject", text);
    }

    public String getBody() {
        String body = getChildBlockText("body");
        XMLDataBlock error = getChildBlock("error");
        if (error == null) {
            return body;
        }
        return body + "Error\n" + XmppError.decodeStanzaError(error).toString();
    }


    public String getOOB() {
        XMLDataBlock oobData = findNamespace("x", "jabber:x:oob");
        StringBuilder oob = new StringBuilder("\n");
        try {
            oob.append(oobData.getChildBlockText("desc"));
            if (oob.length() > 1) {
                oob.append(" ");
            }
            oob.append("( ").append(oobData.getChildBlockText("url")).append(" )");
        } catch (Exception ex) {
            return null;
        }
        return oob.toString();
    }

    public long getMessageTime() {
        try {
            return Time.dateIso8601(findNamespace("x", "jabber:x:delay").getAttribute("stamp"));
        } catch (Exception ignore) {
        }
        try {
            return Time.dateIso8601(findNamespace("delay", "urn:xmpp:delay").getAttribute("stamp"));
        } catch (Exception ignore) {
        }
        return 0; //0 means no timestamp
    }

    public String getTagName() {
        return "message";
    }

    public String getXFrom() {
        try {
            // jep-0033 extended stanza addressing from psi
            XMLDataBlock addresses = getChildBlock("addresses");
            for (Enumeration e = addresses.getChildBlocks().elements(); e.hasMoreElements(); ) {
                XMLDataBlock adr = (XMLDataBlock) e.nextElement();
                if (adr.getTypeAttribute().equals("ofrom")) {
                    return adr.getAttribute("jid");
                }
            }
        } catch (Exception e) { /* normal case if not forwarded message */ }
        return getAttribute("from");
    }

    public String getFrom() {
        return getAttribute("from");
    }
}