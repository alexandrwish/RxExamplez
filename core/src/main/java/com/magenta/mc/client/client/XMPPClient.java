/*
 * XMPPClient.java
 *
 * Created on 6.01.2005, 19:16
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


package com.magenta.mc.client.client;


import com.magenta.mc.client.MobileApp;
import com.magenta.mc.client.components.MCTimerTask;
import com.magenta.mc.client.components.dialogs.DialogCallback;
import com.magenta.mc.client.locale.SR;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.login.AuthFactory;
import com.magenta.mc.client.login.LoginListener;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xml.XMLBlockListener;
import com.magenta.mc.client.xml.XMLDataBlock;
import com.magenta.mc.client.xmpp.XMPPListener;
import com.magenta.mc.client.xmpp.XMPPStream;
import com.magenta.mc.client.xmpp.XmppError;
import com.magenta.mc.client.xmpp.XmppErrorException;
import com.magenta.mc.client.xmpp.datablocks.Iq;
import com.magenta.mc.client.xmpp.datablocks.Message;
import com.magenta.mc.client.xmpp.datablocks.Presence;
import com.magenta.mc.client.xmpp.extensions.IqLast;
import com.magenta.mc.client.xmpp.extensions.IqTimeReply;
import com.magenta.mc.client.xmpp.extensions.IqVersionReply;
import com.magenta.mc.client.xmpp.extensions.Ping;
import com.magenta.mc.client.xmpp.extensions.XMPPTimeResponse;
import com.magenta.mc.client.xmpp.extensions.rpc.JabberRPC;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

/**
 * @author Eugene Stahov
 */
public class XMPPClient implements
        XMPPListener,
        Runnable,
        LoginListener {


    private static final Mutex initLock = new Mutex();
    private static final Object reconnectMutex = new Object();
    private static XMPPClient instance;
    private static Contact self;
    private static Contact server;
    private final Object connectionIdMutex = new Object();
    private Jid myJid;
    /**
     * The stream representing the connection to ther server
     */
    private XMPPStream xmppStream;
    private boolean reconnect = false;
    private boolean querysign = false;
    private int myStatus = Presence.PRESENCE_OFFLINE;
    private boolean autoAway;
    private long lastMessageTime = System.currentTimeMillis();
    private int reconnectCount;
    private boolean isTryToLogin = true;
    private AutoStatusTask autostatus;
    private ConnectionListener connectionListener = ConnectionListener.getInstance();
    private MsgListener msgListener;
    private MCTimerTask connectionTimeout;
    private Runnable afterXmppConflict;
    private PooledExecutor connectionQueue;
    private long connectionId;
    private int lastOnlineStatus;

    /**
     * Creates a new instance of Roster
     * Sets up the stream to the server and adds this class as a listener
     */
    private XMPPClient() {
        String jid = Setup.get().getSettings().getJid();
        setMyJid(new Jid(jid));

        connectionQueue = new PooledExecutor(new LinkedQueue(), 1);
        connectionQueue.setThreadFactory(new ThreadFactory() {
            private int threadNum = 1;

            public Thread newThread(Runnable command) {
                return new Thread(command, "Connection-" + threadNum++);
            }
        });
        connectionQueue.setKeepAliveTime(60000);
    }

    public static XMPPClient getInstance() {
        if (instance == null) {
            try {
                initLock.acquire();
                if (instance == null) {
                    instance = new XMPPClient();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                initLock.release();
            }
        }
        return instance;
    }

    public void setMsgListener(MsgListener msgListener) {
        this.msgListener = msgListener;
    }

    public boolean isTryToLogin() {
        return isTryToLogin;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void start() {
        // connect to a server
        if (Setup.get().getSettings().isAutoLogin()) {
            sendPresence(Presence.PRESENCE_ONLINE);
        }

        // schedule auto-away
        if (Setup.get().getSettings().isAutoAway()) {
            autostatus = new AutoStatusTask();
        }
    }

    public void stop() {
        isTryToLogin = true;
        disconnect();
    }

    private boolean isAlreadyReconnectingForConnection(long id) {
        synchronized (connectionIdMutex) {
            if (connectionId == id) {
                connectionId = 0;
                return false;
            }
            return true;
        }
    }

    private void setConnectionId(long id) {
        synchronized (connectionIdMutex) {
            connectionId = id;
        }
    }

    private void connect() {
        try {
            connectionQueue.execute(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (xmppStream != null) { // not closed yet
            try {
                xmppStream.close(); // sends </stream:stream> and closes socket
            } catch (Exception e) {
                e.printStackTrace();
            }
            xmppStream = null;
            System.gc();
            connectionListener.disconnected();
        }
    }

    /*
        establishing connection process
    */
    public void run() {
        long connectionId = System.currentTimeMillis();
        MCLoggerFactory.getLogger(getClass()).debug("Starting client thread, connection id: " + connectionId);
        setConnectionId(connectionId);
        if (Login.isLogout()) {
            MCLoggerFactory.getLogger(getClass()).debug("Reconnecting after logout, stopping client thread");
            // wee seem to be reconnecting after logout - no destiny for us
            return;
        }

        scheduleConnectionTimeout(connectionId); // starting new connection - schedule timeout

        ConnectionListener.startConnecting();
        setQuerySign(true);

        try {
            setProgress(SR.MS_CONNECT_TO + "server", 30);
            SR.loaded();
            xmppStream = openJabberStream(connectionId);
            setProgress(SR.MS_OPENING_STREAM, 40);
            xmppStream.setJabberListener(this);
            xmppStream.initiateStream();
        } catch (Exception e) {
            connectionTerminated(e, connectionId);
        }
    }

    protected XMPPStream openJabberStream(long connectionId) throws java.io.IOException {

        /* todo: check if the following necessary:
        io.DnsSrvResolver dns=new io.DnsSrvResolver();
        if (dns.getSrv(host)) {
            host=dns.getHost();
            port=dns.getPort();
        }*/

//        String host = Setup.get().getSettings().getHost();
        String host = Setup.get().getSettings().getCurrentHost();

        // todo: check that host address is not empty
        /*if (host == null || (host = host.trim()).length() == 0) {

            throw new RuntimeException();
        }*/
        MCLoggerFactory.getLogger(getClass()).info("Connect to host: " + host);
        int port = Setup.get().getSettings().getPort();
        boolean ssl = Setup.get().getSettings().useSSL();
        final XMPPStream stream = MobileApp.getInstance().initStream(Setup.get().getSettings().getServerName(), host, port, ssl, connectionId);
        return stream != null ? stream : new XMPPStream(Setup.get().getSettings().getServerName(), host, port, ssl, connectionId);
    }

    public XMPPStream getXmppStream() {
        return xmppStream;
    }

    public void setProgress(String pgs, int percent) {
        // todo: do we need progress-bar?
        /*SplashScreen.getInstance().setProgress(pgs, percent);
        setRosterTitle(pgs);*/
    }

    public void setQuerySign(boolean requestState) {
        // todo:  some sign could be shown indicating that we querying the server
        querysign = requestState;
        //updateTitle();
    }

    public void send(XMLDataBlock block) {
        if (xmppStream != null) {
            xmppStream.send(block);
        } // todo: else { consider saving packets (with timeouts?) until available connection }
    }

    /**
     * Method to inform the server we are now online
     */
    public void sendPresence(int newStatus) {

        if (newStatus != Presence.PRESENCE_SAME) {
            myStatus = newStatus;
        }

        setQuerySign(false);

        if (myStatus != Presence.PRESENCE_OFFLINE) {
            lastOnlineStatus = myStatus;
        }

        // reconnect if disconnected
        if (myStatus != Presence.PRESENCE_OFFLINE && isDisconnected()) {
            connect();
            return;
        }

        if (isLoggedIn()) {
            // send presence
            final int priority = 0; // not dealing with priority
            final String message = null; // no text status
            Presence presence = new Presence(myStatus, priority, message, Setup.get().getSettings().getNick());
            xmppStream.send(presence);
        }

        // disconnect
        if (myStatus == Presence.PRESENCE_OFFLINE) {
            disconnect();
        }
    }

    /**
     * The same as previous method, sending pre-constructed Presence stanza
     * todo: consider splitting presence management from connection management
     */
    public void sendPresence(Presence presence) {
        final int newStatus = presence.getTypeIndex();

        if (newStatus != Presence.PRESENCE_SAME) {
            myStatus = newStatus;
        }

        final boolean goOffline = myStatus == Presence.PRESENCE_OFFLINE;

        if (!goOffline) {
            lastOnlineStatus = myStatus;
        }

        // reconnect if disconnected
        if (!goOffline && isDisconnected()) {
            connect();
            return;
        }

        if (isLoggedIn()) {
            // send presence
            MCLoggerFactory.getInstance().getLogger(getClass()).debug("Send presence " + presence);
            xmppStream.send(presence);
        } else {
            MCLoggerFactory.getInstance().getLogger(getClass()).debug("Not logged in, don't send: " + presence);
        }

        // disconnect
        if (goOffline) {
            disconnect();
        }
    }

    private boolean isDisconnected() {
        return xmppStream == null;
    }

    /*
        for sending subscription presence
     */
    public void sendPresence(String to, String type, XMLDataBlock child) {
        //ExtendedStatus es= StatusList.getInstance().getState(myStatus);
        XMLDataBlock presence = new Presence(to, type);
        //Presence presence = new Presence(myStatus, es.getPriority(), es.getMessage());
        if (child != null) {
            presence.addChild(child);
        }
        //presence.setTo(to);
        xmppStream.send(presence);
    }

    /*
        for sending to conference or specific user
     */
    public void sendDirectPresence(int status, String to, XMLDataBlock x) {
        if (to == null) {
            sendPresence(status);
            return;
        }
        Presence presence = new Presence(status, 0, null, Setup.get().getSettings().getNick());
        presence.setTo(to);

        if (x != null) {
            presence.addChild(x);
        }

        if (isLoggedIn()) {
            xmppStream.send(presence);
        }
    }

    public boolean isLoggedIn() {
        return !isDisconnected() && xmppStream.loggedIn;
    }

    public Contact selfContact() {
        if (self == null) {
            self = new Contact(Setup.get().getSettings().getNick(), myJid.getJid(), Presence.PRESENCE_ONLINE, null);
        }
        return self;
    }

    public Contact serverContact() {
        if (server == null) {
            server = new Contact(Setup.get().getSettings().getServerNick(), Setup.get().getSettings().getServerJid(), Presence.PRESENCE_ONLINE, null);
        }
        return server;
    }

    public void doSubscribe(Contact c) {
        if (c.subscr == null) {
            return;
        }
        boolean subscribe =
                c.subscr.startsWith("none") ||
                        c.subscr.startsWith("from");
        if (c.ask_subscribe) {
            subscribe = false;
        }

        boolean subscribed =
                c.subscr.startsWith("none") ||
                        c.subscr.startsWith("to");
        //getMessage(cursor).messageType==Msg.MESSAGE_TYPE_AUTH;

        String to = c.getBareJid();

        if (subscribed) {
            sendPresence(to, "subscribed", null);
        }
        if (subscribe) {
            sendPresence(to, "subscribe", null);
        }
    }

    /**
     * Method to send a message to the specified recipient
     */

    public void sendMessage(Contact to, String id, final String body, final String subject, String composingState) {
        boolean groupchat = to.origin == Contact.ORIGIN_GROUPCHAT;
        Message message = new Message(
                to.getJid(),
                body,
                subject,
                groupchat
        );
        message.setAttribute("id", id);
        if (groupchat && body == null && subject == null) {
            return;
        }

        if (composingState != null) {
            message.addChildNs(composingState, "http://jabber.org/protocol/chatstates");
        }


        if (!groupchat) {
            if (body != null && isEventDelivery()) {
                message.addChildNs("request", "urn:xmpp:receipts");
            }
        }

        xmppStream.send(message);
        lastMessageTime = System.currentTimeMillis();
    }

    private boolean isEventDelivery() {
        final boolean eventDelivery = false; // todo: check (settings?)
        return eventDelivery;
    }

    private void sendDeliveryMessage(Contact c, String id) {
        if (!isEventDelivery()) {
            return;
        }
        if (myStatus == Presence.PRESENCE_INVISIBLE) {
            return;
        }
        Message message = new Message(c.jid.getJid());

        //xep-0184
        message.setAttribute("id", id);
        message.addChildNs("received", "urn:xmpp:receipts");
        xmppStream.send(message);
    }

    /**
     * Method to handle an incomming datablock.
     *
     * @param error error string
     */

    public void loginFailed(String error) {
        if (Login.isUserLoggedIn() && Setup.get().getSettings().getIntProperty("reconnect.count", "3") < 0) {
            // application is set up to reconnect infinitely, and user has already logged in
            // so let it try on
            MCLoggerFactory.getLogger(getClass()).warn("Login failed, but keep reconnecting as of settings (" + error + ")");
        } else {
            dropConnectionTimeout();

            myStatus = Presence.PRESENCE_OFFLINE;
            setProgress(SR.MS_LOGIN_FAILED, 0);

            MCLoggerFactory.getLogger(getClass()).warn("Login failed: " + error);

            disconnect();

            setQuerySign(false);
        }
        synchronized (reconnectMutex) {
            reconnect = false;
        }
    }

    private void scheduleConnectionTimeout(final long connectionId) {
        if (connectionTimeout != null) {
            MCLoggerFactory.getLogger(getClass()).debug("Warning: scheduling next connection timeout while previous exists");
            dropConnectionTimeout(); // just in case if previous timeout scheduled
        }

        connectionTimeout = new MCTimerTask() {

            public void runTask() {
                connectionTimeout = null;
                connectionTerminated(new RuntimeException("MC Connection Timeout: " + connectionId), connectionId);
            }

            public boolean cancel() {
                MCLoggerFactory.getLogger(getClass()).debug("connection timeout cancelled: " + connectionId);
                return super.cancel();
            }
        };
        long timeout = 0;
        if (isTryToLogin) {
            timeout = (long) Setup.get().getSettings().getIntProperty("first.login.timeout", "30") * 1000;
        } else {
            timeout = (long) Setup.get().getSettings().getIntProperty("login.timeout", "30") * 1000;
        }
        MobileApp.getInstance().getTimer().schedule(connectionTimeout, timeout);
        MCLoggerFactory.getLogger(getClass()).debug("connection timeout (" + (timeout / 1000) + "s) scheduled: " + connectionId);
    }

    private void dropConnectionTimeout() {
        if (connectionTimeout != null) {
            connectionTimeout.cancel();
            connectionTimeout = null;
        }
    }

    public void loginSuccess(boolean initiatedByUser) {
        dropConnectionTimeout();

        xmppStream.addBlockListener(new XMPPTimeResponse());
        xmppStream.addBlockListener(new IqLast());
        xmppStream.addBlockListener(new IqVersionReply());
        xmppStream.addBlockListener(new IqTimeReply());
        //xmppStream.addBlockListener(new EntityCaps());
        xmppStream.addBlockListener(new Ping());
        xmppStream.addBlockListener(JabberRPC.getInstance());

        //enable keep-alive packets
        xmppStream.restartKeepAliveTask();

        xmppStream.loggedIn = true;
        reconnectCount = 0;
        isTryToLogin = false;

        //connectionListener.connected();

        // залогинились. теперь, если был реконнект, то просто пошлём статус
        /*if (reconnect) {
            querysign = reconnect = false;
            sendPresence(myStatus);
            return;
        } else {
            // иначе будем читать ростер
            //xmppStream.enableRosterNotify(true);
        }*/

        synchronized (reconnectMutex) {
            reconnect = false;
        }

    }

    public void bindResource(String myJid) {
        Contact self = selfContact();
        self.jid = this.myJid = new Jid(myJid);
    }

    public int blockArrived(XMLDataBlock data) {
        try {

            if (data instanceof Iq) {
                String from = data.getAttribute("from");
                String type = data.getTypeAttribute();
                String id = data.getAttribute("id");

                if (id != null) { // some protocol responses

                    if (id.startsWith("nickvc")) { // something with nick
                        return XMLBlockListener.BLOCK_PROCESSED;
                    }

                    if (id.startsWith("getvc")) { // vcard
                        return XMLBlockListener.BLOCK_PROCESSED;
                    }

                    if (id.equals("getros")) { // roster
                        if (type.equals("result")) {
                            return XMLBlockListener.BLOCK_PROCESSED;
                        }
                    }

                } // id!=null
                if (type.equals("result")) {
                    // the results are handled in iqHandlers
                } else if (type.equals("set")) {
                    // this could be roster, but not using it
                }
            } //if( data instanceof Iq )

            // If we've received a message

            else if (data instanceof Message) {
                querysign = false;
                boolean highlite = false;
                Message message = (Message) data;

                String from = message.getFrom();
                //Enable forwarding only from self-jids
                if (myJid.equals(new Jid(from), false)) {
                    from = message.getXFrom();
                }
                String body = message.getBody().trim();
                String oob = message.getOOB();
                String type = message.getTypeAttribute();

                if (oob != null) {
                    body += oob;
                }
                if (body.length() == 0) {
                    body = null;
                }
                String subj = message.getSubject().trim();
                if (subj.length() == 0) {
                    subj = null;
                }
                long tStamp = message.getMessageTime();

                int start_me = -1;    //  не добавлять ник
                String name = null;
                boolean groupchat = false;

                int mType = Msg.MESSAGE_TYPE_IN;

                try { // type=null
                    if (type.equals("groupchat")) {
                        groupchat = true;
                        start_me = 0; // добавить ник в начало
                        int rp = from.indexOf('/');

                        name = from.substring(rp + 1);

                        if (rp > 0) {
                            from = from.substring(0, rp);
                        }

                        // subject
                        if (subj != null) {
                            if (body == null) {
                                body = name + SR.MS_HAS_CHANGED_SUBJECT_TO + subj;
                            }
                            subj = null;
                            start_me = -1; // не добавлять /me к subj
                            highlite = true;
                            mType = Msg.MESSAGE_TYPE_SUBJ;
                        }
                    }
                    if (type.equals("error")) {

                        body = SR.MS_ERROR_ + XmppError.findInStanza(message).toString();

                        //TODO: verify and cleanup
                        //String errCode=message.getChildBlock("error").getAttribute("code");
                        //
                        //switch (Integer.parseInt(errCode)) {
                        //    case 403: body=SR.MS_VIZITORS_FORBIDDEN; break;
                        //    case 503: break;
                        //    default: body=SR.MS_ERROR_+message.getChildBlock("error")+"\n"+body;
                        //}
                    }
                    if (type.equals("headline")) {
                        mType = Msg.MESSAGE_TYPE_HEADLINE;
                    }
                } catch (Exception e) {
                    type = "chat";
                } //force type to chat

                /*try {
       XMLDataBlock xmlns=message.findNamespace("x", "http://jabber.org/protocol/muc#user");
       XMLDataBlock error=xmlns.getChildBlock("error");
       XMLDataBlock invite=xmlns.getChildBlock("invite");
       // FS#657
       if (invite !=null) {
           if (error!=null ) {
               ConferenceGroup invConf=(ConferenceGroup)groups.getGroup(from);
               body=XmppError.decodeStanzaError(error).toString(); *//*"error: invites are forbidden"*//*
                        } else {
                            String inviteFrom=invite.getAttribute("from");
                            String inviteReason=invite.getChildBlockText("reason");
                            String room=from+'/'+sd.account.getNickName();
                            String password=xmlns.getChildBlockText("password");
                            ConferenceGroup invConf=initMuc(room, password);

                            invConf.getConference().commonPresence=false; //FS#761

                            if (invConf.getSelfContact().status==Presence.PRESENCE_OFFLINE)
                                invConf.getConference().status=Presence.PRESENCE_OFFLINE;

                            body=inviteFrom+SR.MS_IS_INVITING_YOU+from+" ("+inviteReason+')';
                        }
                    }
                } catch (Exception e) { *//* not muc#user case*//* }*/

                //Passenger c = getContact(from, cf.notInListDropLevel != NotInListFilter.DROP_MESSAGES_PRESENCES);
                Contact c = serverContact(); // todo: проверить, точно ли тут нужен контакт сервера?

                if (c == null) {
                    return XMLBlockListener.BLOCK_REJECTED;
                } //not-in-list message dropped

                if (name == null) {
                    name = c.getName();
                }
                // /me

                if (body != null) {
                    if (body.startsWith("/me ")) {
                        start_me = 3;
                    }
                    if (start_me >= 0) {
                        StringBuffer b = new StringBuffer("\01");
                        b.append(name)
                                .append("\02");
                        if (start_me == 0) {
                            b.append("> ");
                        } else {
                            b.insert(0, '*');
                        }
                        b.append(body.substring(start_me));
                        body = b.toString();
                    }
                }

                if (type.equals("chat")) {
                    if (message.findNamespace("request", "urn:xmpp:receipts") != null) {
                        sendDeliveryMessage(c, data.getAttribute("id"));
                    }

                    if (message.findNamespace("received", "urn:xmpp:receipts") != null) {
                        c.markDelivered(data.getAttribute("id"));
                    }

                    if (message.findNamespace("active", "http://jabber.org/protocol/chatstates") != null) {
                        c.acceptComposing = true;
                        c.setComposing(false);
                    }

                    if (message.findNamespace("paused", "http://jabber.org/protocol/chatstates") != null) {
                        c.acceptComposing = true;
                        c.setComposing(false);
                    }

                    if (message.findNamespace("composing", "http://jabber.org/protocol/chatstates") != null) {
                        c.acceptComposing = true;
                        c.setComposing(true);
                    }
                }

                //redraw();

                if (body == null) {
                    return XMLBlockListener.BLOCK_REJECTED;
                }

                Msg m = new Msg(mType, from, subj, body);
                if (tStamp != 0) {
                    m.dateGmt = tStamp;
                }
                if (m.getBody().indexOf(SR.MS_IS_INVITING_YOU) > -1) {
                    m.dateGmt = 0;
                }
                /*if (groupchat) {
                    ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                    if (mucGrp.getSelfContact().getJid().equals(message.getFrom())) {
                        m.messageType=Msg.MESSAGE_TYPE_OUT;
                        m.unread=false;
                    } else {
                        if (m.dateGmt<= ((ConferenceGroup)c.getGroup()).conferenceJoinTime) m.messageType=Msg.MESSAGE_TYPE_HISTORY;
                        // highliting messages with myNick substring
                        String myNick=mucGrp.getSelfContact().getName();
                        highlite |= body.indexOf(myNick)>-1;
                        //TODO: custom highliting dictionary
                    }
                    m.from=name;
                }*/
                m.setHighlite(highlite);

                //if (c.getGroupType()!=Groups.TYPE_NOT_IN_LIST || cf.notInList)
                messageStore(c, m);
                /*new MessageDialog(
                        MobileApp.getInstance().getCurrentFrame(),
                        MobileApp.localize("incoming_message"),
                        m.getBody(),
                        true).show();*/

                return XMLBlockListener.BLOCK_PROCESSED;
            }
            // присутствие

            else if (data instanceof Presence) {
                // not handling incoming presence
                return XMLBlockListener.BLOCK_PROCESSED;
            } // if presence
        } catch (Exception e) {
            e.printStackTrace();
        }
        return XMLBlockListener.BLOCK_REJECTED;
    }

    public void rosterItemNotify() {
        // no one seems to use me, check this and kill me
    }

    public void messageStore(Contact c, Msg message) {
        // could store messages here - not storing currently
        if (c == null) {
            return;
        }
        c.addMessage(message);

        if (!message.unread) {
            return;
        }

        if (message.messageType != Msg.MESSAGE_TYPE_HISTORY) {
            playNotify(0);
            notifyMessage(message);
        }
    }

    public void playNotify(int event) {

        /*String message=cf.messagesnd;
    String type=cf.messageSndType;
	int volume=cf.soundVol;
        int profile=cf.profile;
        if (profile==AlertProfile.AUTO) profile=AlertProfile.ALL;

        EventNotify notify=null;

        boolean blFlashEn=cf.blFlash;   // motorola e398 backlight bug

        switch (profile) {
            case AlertProfile.ALL:   notify=new EventNotify(display, type, message, cf.vibraLen, blFlashEn); break;
            case AlertProfile.NONE:  notify=new EventNotify(display, null, null,    0,           false    ); break;
            case AlertProfile.VIBRA: notify=new EventNotify(display, null, null,    cf.vibraLen, blFlashEn); break;
            case AlertProfile.SOUND: notify=new EventNotify(display, type, message, 0,           blFlashEn); break;
        }
        if (notify!=null) notify.startNotify();*/
    }

    private void notifyMessage(Msg msg) {
        if (msgListener != null) {
            msgListener.incoming(msg);
        }
    }

    /**
     * Method to begin talking to the server (i.e. send a login message)
     */
    public void beginConversation() {
        setProgress("MS_LOGINPGS", 42);
        AuthFactory.createAuth();
    }

    /**
     * If the connection is terminated then print a message
     *
     * @e The exception that caused the connection to be terminated, Note that
     * receiving a SocketException is normal when the client closes the stream.
     */
    public void connectionTerminated(final Exception e, long connectionId) {
        MCLoggerFactory.getLogger(getClass()).debug("Connection terminated: " + e.getMessage());
        synchronized (reconnectMutex) {
            if (reconnect) {
                MCLoggerFactory.getLogger(getClass()).debug("already reconnecting, continue");
            } else {
                dropConnectionTimeout();
                connectionListener.disconnected();
                setProgress(SR.MS_DISCONNECTED, 0);
                if (!isAlreadyReconnectingForConnection(connectionId)) {
                    MCLoggerFactory.getLogger(getClass()).debug("askReconnect id: " + connectionId + e.getMessage());
                    reconnect = true;
                    askReconnect(e);
                } else {
                    MCLoggerFactory.getLogger(getClass()).debug("don't invoke askReconnect id: " + connectionId + e.getMessage());
                }
            }
        }
    }

    public void setXmppConflictListener(Runnable afterReconnect) {
        this.afterXmppConflict = afterReconnect;
    }

    private void askReconnect(final Exception e) {
        // todo: uncomment if supporting SSL (consider using SE API)
        /*String errSSL = com.magenta.mc.client.io.SSLExceptionDecoder.decode(e);
        if (errSSL != null && errSSL.length() > 0) {
            // SSL error
            MCLoggerFactory.getLogger(getClass()).debug("SSL error: " + errSSL);
        }*/

        disconnect();

        if (e instanceof SecurityException) {
            reconnect = false;
            return;
        }

        if (e instanceof XmppErrorException) {
            XmppErrorException xe = (XmppErrorException) e;
            final XmppError xmppError = xe.getError();
            if (xmppError != null && xmppError.getCondition() == XmppError.CONFLICT) {
                Setup.get().getUI().getDialogManager().asyncMessageSafe(
                        MobileApp.localize("xmpp.conflict"),
                        MobileApp.localize("xmpp.conflict.reconnect"),
                        new DialogCallback() {
                            public void done(boolean ok) {
                                if (afterXmppConflict != null) {
                                    afterXmppConflict.run();
                                }
                            }
                        });
                ConnectionListener.stopConnecting();
                connectionListener.disconnected();
                reconnect = false;
                return;
            }
        }

        int reconnectSetting = Setup.get().getSettings().getIntProperty("reconnect.count", "3");
        // reconnectSetting = -1 means reconnecting infinitly
        // whether reconnect count is given or we're doing login
        int maxReconnect = reconnectSetting;
        if (isTryToLogin) {
            // if this is a login let's try to reconnect twice
            maxReconnect = Setup.get().getSettings().getIntProperty("first.login.reconnect.count", "2");
        }

        Setup.get().getSettings().switchHost();
        if (maxReconnect < 0) {
            reconnectCount++;
            // infinite reconnect count given, just reconnect
        } else if (reconnectCount >= maxReconnect) {
            reconnectCount = 0;
            if (!isTryToLogin && Setup.get().getUI().getDialogManager().confirmSafe(MobileApp.localize("connection.failed"), MobileApp.localize("connection.failed.reconnect"))) {
                // restart reconnection
            } else {
                ConnectionListener.stopConnecting();
                connectionListener.disconnected();
                reconnect = false;
                return;
            }
        } else {
            reconnectCount++;
        }
        new Reconnect(reconnectCount == 1);
    }

    public void doReconnect() {
        MCLoggerFactory.getLogger(getClass()).debug("XMPPClient.doReconnect()");
        try {
            sendPresence(lastOnlineStatus);
        } finally {
            synchronized (reconnectMutex) {
                reconnect = false;
            }
        }
    }

    /*
        уведомлять этот метод о пользовательской активности
        (нужно при autoAway чтобы сбрасывать таймер)
    */
    public void userActivity() {
        if (Setup.get().getSettings().isAutoAway()) {
            autostatus.restart();
        }
        setAutoStatus(Presence.PRESENCE_ONLINE);
    }

    public void logoff() {
        if (isLoggedIn()) {
            try {
                //sendPresence(Presence.PRESENCE_OFFLINE);
                disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loginMessage(String msg) {
        setProgress(msg, 42);
    }

    public void setMyJid(Jid myJid) {
        this.myJid = myJid;
    }

    public void setAutoStatus(int status) {
        if (!isLoggedIn()) {
            return;
        }
        if (status == Presence.PRESENCE_ONLINE && autoAway) {
            autoAway = false;
            sendPresence(Presence.PRESENCE_ONLINE);
            return;
        }
        if (status != Presence.PRESENCE_ONLINE && myStatus == Presence.PRESENCE_ONLINE && !autoAway) {
            autoAway = true;
            sendPresence(Presence.PRESENCE_AWAY);
        }
    }

    public interface MsgListener {
        void incoming(Msg msg);
    }
}

