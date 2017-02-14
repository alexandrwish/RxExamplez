package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.rpc.xmpp.datablocks.Presence;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Created 01.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class Contact {
    public final static byte ORIGIN_ROSTER = 0;
    public final static byte ORIGIN_ROSTERRES = 1;
    public final static byte ORIGIN_CLONE = 2;
    public final static byte ORIGIN_PRESENCE = 3;
    public final static byte ORIGIN_GROUPCHAT = 4;
    public final static byte ORIGIN_GC_MEMBER = 5;
    public final static byte ORIGIN_GC_MYSELF = 6;

    /*public final static String XEP184_NS="http://www.xmpp.org/extensions/xep-0184.html#ns";
    public final static int DELIVERY_NONE=0;
    public final static int DELIVERY_HANDSHAKE=1;
    public final static int DELIVERY_XEP184=2;
    public final static int DELIVERY_XEP22=3;*/
    public String nick;
    public Jid jid;
    public String bareJid;    // for roster/subscription manipulating
    public int priority;
    //private Group group;
    public int transport;
    public boolean acceptComposing;
    public Integer incomingComposing;
    public int deliveryType;
    public String msgSuspended;
    public byte origin;
    public String subscr;
    public int offline_type = Presence.PRESENCE_UNKNOWN;
    public boolean ask_subscribe;
    public Vector msgs;
    public int unreadType;
    //public boolean gcMyself;
    public int lastUnread;
    public int pepMood = -1;
    public boolean pepTune;
    protected int status;
    //public int key1;
    protected int key0;
    protected String key1;
    private boolean isActive;

    //public VCard vcard;
    private int newMsgCnt = -1;

    /**
     * Creates a new instance of Contact
     */
    protected Contact() {
        msgs = new Vector();
        key1 = "";
    }
    //public long conferenceJoinTime;

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick = Nick;
        jid = new Jid(sJid);
        status = Status;
        bareJid = sJid;
        this.subscr = subscr;

        setSortKey((Nick == null) ? sJid : Nick);
        //msgs.removeAllElements();

        //calculating transport
        //transport=RosterIcons.getInstance().getTransportIndex(jid.getTransport());
    }

    public int firstUnread() {
        int unreadIndex = 0;
        for (Enumeration e = msgs.elements(); e.hasMoreElements(); ) {
            if (((Msg) e.nextElement()).unread) {
                break;
            }
            unreadIndex++;
        }
        return unreadIndex;
    }

    public Contact clone(Jid newjid, final int status) {
        Contact clone = new Contact();
        //clone.group=group;
        clone.jid = newjid;
        clone.nick = nick;
        clone.key1 = key1;
        clone.subscr = subscr;
        clone.offline_type = offline_type;
        clone.origin = ORIGIN_CLONE;
        clone.status = status;
        //clone.transport=RosterIcons.getInstance().getTransportIndex(newjid.getTransport()); //<<<<

        clone.pepMood = pepMood;
        clone.pepTune = pepTune;

        clone.bareJid = bareJid;
        return clone;
    }

    public int getImageIndex() {
        /*if (getNewMsgsCount()>0)
            switch (unreadType) {
                case Msg.MESSAGE_TYPE_AUTH: return RosterIcons.ICON_AUTHRQ_INDEX;
                default: return RosterIcons.ICON_MESSAGE_INDEX;
            }
        if (incomingComposing!=null) return RosterIcons.ICON_COMPOSING_INDEX;
        int st=(status==Presence.PRESENCE_OFFLINE)?offline_type:status;
        if (st<8) st+=transport;
        return st;*/
        return 0;
    }

//#ifdef PEP
    /*public void drawItem(Graphics g, int ofs, boolean sel) {
        int w=g.getClipWidth();
        int h=g.getClipHeight();
        int xo=g.getClipX();
        int yo=g.getClipY();

        if (pepMood>=0) {
            ImageList moods=MoodIcons.getInstance();
            w-=moods.getWidth();
            moods.drawImage(g, pepMood, w,0);
        }

        if (pepTune) {
            w-=il.getWidth();
            il.drawImage(g, RosterIcons.ICON_PROFILE_INDEX+3, w,0);
        }

        g.setClip(xo, yo, w, h);

        super.drawItem(g, ofs, sel);
    }*/
//#endif

    public int getNewMsgsCount() {
        //if (getGroupType()==Groups.TYPE_IGNORE) return 0;
        //return msgs.size()-lastReaded;
        if (newMsgCnt > -1) {
            return newMsgCnt;
        }
        int nm = 0;
        unreadType = Msg.MESSAGE_TYPE_IN;
        for (Enumeration e = msgs.elements(); e.hasMoreElements(); ) {
            Msg m = (Msg) e.nextElement();
            if (m.unread) {
                nm++;
                if (m.messageType == Msg.MESSAGE_TYPE_AUTH) {
                    unreadType = m.messageType;
                }
            }
        }
        return newMsgCnt = nm;
    }

    //public boolean needsCount(){ return (newMsgCnt<0);  }

    public boolean active() {
        return isActive;
    }

    public void resetNewMsgCnt() {
        newMsgCnt = -1;
    }

    public void setComposing(boolean state) {
        //incomingComposing=(state)? new Integer(RosterIcons.ICON_COMPOSING_INDEX):null;
        //System.out.println("Composing:"+state);
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
            lastUnread = msgs.size() - 1;
            if (m.messageType > unreadType) {
                unreadType = m.messageType;
            }
            if (newMsgCnt >= 0) {
                newMsgCnt++;
            }
        }
    }

    public int getFontIndex() {
        return (status < 5) ? 1 : 0;
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
    //public void onSelect(){}

    public final String getJid() {
        return jid.getJid();
    }

    public final String getBareJid() {
        return bareJid;
    }

    public String getNickJid() {
        if (nick == null) {
            return bareJid;
        }
        return nick + " <" + bareJid + ">";
    }

    /**
     * Splits string like "name@jabber.ru/resource" to vector
     * containing 2 substrings
     *
     * @return Vector.elementAt(0)="name@jabber.ru"
     * Vector.elementAt(1)="resource"
     */
    /*
     public static final Vector SplitJid(final String jid) {
        Vector result=new Vector();
        int i=jid.lastIndexOf('/');
        if (i==-1){
            result.addElement(jid);
            result.addElement(null);
        } else {
            result.addElement(jid.substring(0,i));
            result.addElement(jid.substring(i+1));
        }
        return result;
    }
     */
    public final void purge() {
        msgs = new Vector();
        //vcard=null;
        resetNewMsgCnt();
        isActive = false;
    }

    public final void setSortKey(String sortKey) {
        key1 = (sortKey == null) ? "" : sortKey.toLowerCase();
    }

    public String getTipString() {
        int nm = getNewMsgsCount();
        if (nm != 0) {
            return String.valueOf(nm);
        }
        if (nick != null) {
            return bareJid;
        }
        return null;
    }

    /*public Group getGroup() { return group; }
    public int getGroupType() {
        if (group==null) return 0;
        return group.type;
    }
    public boolean inGroup(Group ingroup) {  return group==ingroup;  }

    public void setGroup(Group group) { this.group = group; }*/

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

    public String toClipBoardString() {
        return getJid();
    }
}
