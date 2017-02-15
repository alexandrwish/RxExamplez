package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.mc.util.Time;

public class Msg {

    // without signaling
    public final static int MESSAGE_TYPE_PRESENCE = 2;
    public final static int MESSAGE_TYPE_HISTORY = 3;
    // with signaling
    public final static int MESSAGE_TYPE_IN = 10;
    public final static int MESSAGE_TYPE_HEADLINE = 11;
    public final static int MESSAGE_TYPE_SUBJ = 12;
    public boolean delivered;
    public String id;
    public int messageType;
    public String from;
    public String subject;
    public long dateGmt;
    public boolean unread = false;
    private String body;

    public Msg(int messageType, String from, String subj, String body) {
        this.messageType = messageType;
        this.from = from;
        this.body = body;
        this.subject = subj;
        this.dateGmt = Time.utcTimeMillis();
        if (messageType >= MESSAGE_TYPE_IN) {
            unread = true;
        }
    }

    public String getTime() {
        return Time.timeLocalString(dateGmt);
    }


    public int getColor() {
        return 0;
    }

    public String toString() {
        StringBuilder time = new StringBuilder();
        if (messageType == MESSAGE_TYPE_PRESENCE) {
            time.append("[").append(getTime()).append("] ");
        }
        time.append(body);
        return time.toString();
    }

    public boolean isPresence() {
        return messageType == MESSAGE_TYPE_PRESENCE;
    }

    public String getBody() {
        return body;
    }

    void setHighlite(boolean state) {
    }
}