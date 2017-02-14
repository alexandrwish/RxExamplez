package com.magenta.mc.client.android.rpc.xmpp.datablocks;

import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.util.Vector;

public class Presence extends XMLDataBlock {

    public final static int PRESENCE_ONLINE = 0;
    public final static int PRESENCE_CHAT = 1;
    public final static int PRESENCE_AWAY = 2;
    public final static int PRESENCE_XA = 3;
    public final static int PRESENCE_DND = 4;
    public final static int PRESENCE_OFFLINE = 5;
    public final static int PRESENCE_ASK = 6;
    public final static int PRESENCE_UNKNOWN = 7;
    public final static int PRESENCE_INVISIBLE = 8;
    public final static int PRESENCE_ERROR = 9;
    public final static int PRESENCE_TRASH = 10;
    public final static int PRESENCE_AUTH = -1;
    public final static int PRESENCE_AUTH_ASK = -2;
    public final static int PRESENCE_SAME = -100;

    public final static String PRS_OFFLINE = "unavailable";
    public final static String PRS_ERROR = "error";
    public final static String PRS_CHAT = "chat";
    public final static String PRS_AWAY = "away";
    public final static String PRS_XA = "xa";
    public final static String PRS_DND = "dnd";
    public final static String PRS_ONLINE = "online";
    public final static String PRS_INVISIBLE = "invisible";

    private StringBuffer text;
    private int presenceCode;

    public Presence(XMLDataBlock _parent, Vector _attributes) {
        super(_parent, _attributes);
    }

    public Presence(String to, String type) {
        super(null, null);
        setAttribute("to", to);
        setAttribute("type", type);
    }

    public Presence(int status, int priority, String message, String nick) {
        super(null, null);
        switch (status) {
            case PRESENCE_OFFLINE:
                setType(PRS_OFFLINE);
                break;
            case PRESENCE_INVISIBLE:
                setType(PRS_INVISIBLE);
                break;
            case PRESENCE_CHAT:
                setShow(PRS_CHAT);
                break;
            case PRESENCE_AWAY:
                setShow(PRS_AWAY);
                break;
            case PRESENCE_XA:
                setShow(PRS_XA);
                break;
            case PRESENCE_DND:
                setShow(PRS_DND);
                break;
        }
        this.presenceCode = status;
        if (priority != 0) {
            addChild("priority", String.valueOf(priority));
        }
        if (message != null) {
            if (message.length() > 0) {
                addChild("status", message);
            }
        }
        if (status != PRESENCE_OFFLINE) {
            //addChild(EntityCaps.presenceEntityCaps());
            if (nick != null) {
                addChildNs("nick", "http://jabber.org/protocol/nick").setText(nick);
            }
        }
    }

    public void setType(String type) {
        setAttribute("type", type);
    }

    public void setTo(String jid) {
        setAttribute("to", jid);
    }

    public int getPriority() {
        try {
            return Integer.parseInt(getChildBlockText("priority"));
        } catch (Exception e) {
            return 0;
        }
    }

    public String getTagName() {
        return "presence";
    }

    public int getTypeIndex() {
        return presenceCode;
    }

    public String getPresenceTxt() {
        return text.toString();
    }

    private String getShow() {
        String show = getChildBlockText("show");
        return (show.length() == 0) ? PRS_ONLINE : getChildBlockText("show");
    }

    public void setShow(String text) {
        addChild("show", text);
    }

    public String getFrom() {
        return getAttribute("from");
    }

    public void setFrom(String jid) {
        setAttribute("from", jid);
    }
}