package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.rpc.xmpp.datablocks.Presence;

import java.util.Enumeration;
import java.util.Vector;

public class Contact {

    final static byte ORIGIN_GROUPCHAT = 4;
    private final static byte ORIGIN_CLONE = 2;
    public Jid jid;
    public int priority;
    public boolean acceptComposing;
    public byte origin;
    public String subscr;
    public boolean ask_subscribe;
    protected int status;
    private String nick;
    private String bareJid;    // for roster/subscription manipulating
    private int offline_type = Presence.PRESENCE_UNKNOWN;
    private Vector msgs;
    private int unreadType;
    private int pepMood = -1;
    private boolean pepTune;
    private String key1;
    private boolean isActive;

    private int newMsgCnt = -1;

    protected Contact() {
        msgs = new Vector();
        key1 = "";
    }

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick = Nick;
        jid = new Jid(sJid);
        status = Status;
        bareJid = sJid;
        this.subscr = subscr;
        setSortKey((Nick == null) ? sJid : Nick);
    }

    public Contact clone(Jid newjid, final int status) {
        Contact clone = new Contact();
        clone.jid = newjid;
        clone.nick = nick;
        clone.key1 = key1;
        clone.subscr = subscr;
        clone.offline_type = offline_type;
        clone.origin = ORIGIN_CLONE;
        clone.status = status;
        clone.pepMood = pepMood;
        clone.pepTune = pepTune;
        clone.bareJid = bareJid;
        return clone;
    }

    public boolean active() {
        return isActive;
    }

    public void setComposing(boolean state) {
    }

    public void addMessage(Msg m) {
        boolean first_replace = false;
        if (m.isPresence()) {
            if (msgs.size() == 1) {
                if (((Msg) msgs.firstElement()).isPresence()) {
                    if (origin != ORIGIN_GROUPCHAT) {
                        first_replace = true;
                    }
                }
            }
        }
        if (first_replace) {
            msgs.setElementAt(m, 0);
            return;
        }
        msgs.addElement(m);
        if ((m.messageType != Msg.MESSAGE_TYPE_PRESENCE)
                && (m.messageType != Msg.MESSAGE_TYPE_HISTORY)) {
            isActive = true;
        }
        if (m.unread) {
            if (m.messageType > unreadType) {
                unreadType = m.messageType;
            }
            if (newMsgCnt >= 0) {
                newMsgCnt++;
            }
        }
    }

    public String toString() {
        if (origin > ORIGIN_GROUPCHAT) {
            return nick;
        }
        if (origin == ORIGIN_GROUPCHAT) {
            return getJid();
        }
        return (nick == null) ? getJid() : nick + jid.getResource();
    }

    public final String getName() {
        return (nick == null) ? getBareJid() : nick;
    }

    public final String getJid() {
        return jid.getJid();
    }

    public final String getBareJid() {
        return bareJid;
    }

    private void setSortKey(String sortKey) {
        key1 = (sortKey == null) ? "" : sortKey.toLowerCase();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        setComposing(false);
        this.status = status;
        if (status >= Presence.PRESENCE_OFFLINE) {
            acceptComposing = false;
        }
    }

    void markDelivered(String id) {
        if (id == null) {
            return;
        }
        for (Enumeration e = msgs.elements(); e.hasMoreElements(); ) {
            Msg m = (Msg) e.nextElement();
            if (m.id != null) {
                if (m.id.equals(id)) {
                    m.delivered = true;
                }
            }
        }
    }
}