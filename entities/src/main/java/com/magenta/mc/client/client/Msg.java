package com.magenta.mc.client.client;

import com.magenta.mc.client.util.Time;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created 01.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class Msg {
    // without signaling
    public final static int MESSAGE_TYPE_OUT = 1;
    public final static int MESSAGE_TYPE_PRESENCE = 2;
    public final static int MESSAGE_TYPE_HISTORY = 3;
    // with signaling
    public final static int MESSAGE_TYPE_IN = 10;
    public final static int MESSAGE_TYPE_HEADLINE = 11;
    public final static int MESSAGE_TYPE_ERROR = 11;
    public final static int MESSAGE_TYPE_SUBJ = 12;
    public final static int MESSAGE_TYPE_AUTH = 15;
    public boolean delivered;
    public String id;
    public int messageType;
    /**
     * Отправитель сообщения
     */
    public String from;
    /**
     * Тема сообщения
     */
    public String subject;

    /*public String getMsgHeader(){
        return getTime()+from;
    }*/
    /**
     * Дата сообщения
     */
    public long dateGmt;
    public boolean unread = false;
    //private TimeZone tz(){ return StaticData.getInstance().config.tz;}
    public boolean itemCollapsed;
    public int itemHeight = -1;
    private boolean highlite;
    /**
     * Тело сообщения
     */
    private String body;

    /**
     * Creates a new instance of msg
     */
    public Msg(int messageType, String from, String subj, String body) {
        this.messageType = messageType;
        this.from = from;
        this.body = body;
        this.subject = subj;
        this.dateGmt = Time.utcTimeMillis();
        if (messageType >= MESSAGE_TYPE_IN) {
            unread = true;
        }
        if (messageType == MESSAGE_TYPE_PRESENCE || messageType == MESSAGE_TYPE_HEADLINE) {
            itemCollapsed = true;
        }
        /*if (body!=null && messageType!=MESSAGE_TYPE_SUBJ) {
            if (body.length() > Config.getInstance().messageCollapsedLength)
                itemCollapsed=true;
        }*/
    }

    public Msg(DataInputStream is) throws IOException {
        from = is.readUTF();
        body = is.readUTF();
        dateGmt = is.readLong();
        messageType = MESSAGE_TYPE_IN;
        try {
            subject = is.readUTF();
        } catch (Exception e) {
            subject = null;
        }
    }

    public void onSelect() {
    }

    public String getTime() {
        return Time.timeLocalString(dateGmt);
    }

    public String getDayTime() {
        return Time.dayLocalString(dateGmt) + Time.timeLocalString(dateGmt);
    }

    public int getColor() {
        /*if (highlite)
            if (Config.getInstance().ghostMotor) return Colors.MSG_HIGHLIGHT;
        switch (messageType) {
            case MESSAGE_TYPE_IN: return Colors.MESSAGE_IN;
            case MESSAGE_TYPE_HEADLINE: return Colors.MESSAGE_IN;
            case MESSAGE_TYPE_OUT: return Colors.MESSAGE_OUT;
            case MESSAGE_TYPE_PRESENCE: return Colors.MESSAGE_PRESENCE;
            case MESSAGE_TYPE_AUTH: return Colors.MESSAGE_AUTH;
            case MESSAGE_TYPE_HISTORY: return Colors.MESSAGE_HISTORY;
            case MESSAGE_TYPE_SUBJ:return Colors.MSG_SUBJ;
            //case MESSAGE_TYPE_ERROR: return Colors.MESSAGE_OUT;
        }
        return Colors.LIST_INK;*/
        return 0;
    }

    public String toString() {
        StringBuffer time = new StringBuffer();
        if (messageType == MESSAGE_TYPE_PRESENCE) {
            time.append("[").append(getTime()).append("] ");
        }
        time.append(body);
        return time.toString();
    }

    public String quoteString() {
        StringBuffer out = new StringBuffer(toString());
        int i = 0;
        while (i < out.length()) {
            if (out.charAt(i) < 0x03) {
                out.deleteCharAt(i);
            } else {
                i++;
            }
        }
        return out.toString();
    }

    public boolean isPresence() {
        return messageType == MESSAGE_TYPE_PRESENCE;
    }

    public void serialize(DataOutputStream os) throws IOException {
        os.writeUTF(from);
        os.writeUTF(body);
        os.writeLong(dateGmt);
        if (subject != null) {
            os.writeUTF(subject);
        }
    }

    public String getBody() {
        return body;
    }

    void setHighlite(boolean state) {
        highlite = state;
    }

    public boolean isHighlited() {
        return highlite;
    }
}
