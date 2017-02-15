package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.components.dialogs.DialogCallback;
import com.magenta.mc.client.android.mc.locale.SR;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.login.AuthFactory;
import com.magenta.mc.client.android.mc.login.LoginListener;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.JabberRPC;
import com.magenta.mc.client.android.rpc.extensions.IqLast;
import com.magenta.mc.client.android.rpc.extensions.IqTimeReply;
import com.magenta.mc.client.android.rpc.extensions.IqVersionReply;
import com.magenta.mc.client.android.rpc.extensions.Ping;
import com.magenta.mc.client.android.rpc.extensions.XMPPTimeResponse;
import com.magenta.mc.client.android.rpc.xmpp.XMPPListener;
import com.magenta.mc.client.android.rpc.xmpp.XMPPStream;
import com.magenta.mc.client.android.rpc.xmpp.XmppError;
import com.magenta.mc.client.android.rpc.xmpp.XmppErrorException;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Message;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Presence;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Mutex;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

public class XMPPClient implements XMPPListener, Runnable, LoginListener {

    private static final Mutex initLock = new Mutex();
    private static final Object reconnectMutex = new Object();
    private static XMPPClient instance;
    private static Contact self;
    private static Contact server;
    private final Object connectionIdMutex = new Object();
    private Jid myJid;
    private XMPPStream xmppStream;
    private boolean reconnect = false;
    private int myStatus = Presence.PRESENCE_OFFLINE;
    private boolean autoAway;
    private long lastMessageTime = System.currentTimeMillis();
    private int reconnectCount;
    private boolean isTryToLogin = true;
    private ConnectionListener connectionListener = ConnectionListener.getInstance();
    private MsgListener msgListener;
    private MCTimerTask connectionTimeout;
    private Runnable afterXmppConflict;
    private PooledExecutor connectionQueue;
    private long connectionId;
    private int lastOnlineStatus;

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
        try {
            SR.loaded();
            xmppStream = openJabberStream(connectionId);
            xmppStream.setJabberListener(this);
            xmppStream.initiateStream();
        } catch (Exception e) {
            connectionTerminated(e, connectionId);
        }
    }

    private XMPPStream openJabberStream(long connectionId) throws java.io.IOException {
        String host = Setup.get().getSettings().getCurrentHost();
        MCLoggerFactory.getLogger(getClass()).info("Connect to host: " + host);
        int port = Setup.get().getSettings().getPort();
        boolean ssl = Setup.get().getSettings().useSSL();
        final XMPPStream stream = MobileApp.getInstance().initStream(Setup.get().getSettings().getServerName(), host, port, ssl, connectionId);
        return stream != null ? stream : new XMPPStream(Setup.get().getSettings().getServerName(), host, port, ssl, connectionId);
    }

    public XMPPStream getXmppStream() {
        return xmppStream;
    }

    public void send(XMLDataBlock block) {
        if (xmppStream != null) {
            xmppStream.send(block);
        }
    }

    private void sendPresence(int newStatus) {
        if (newStatus != Presence.PRESENCE_SAME) {
            myStatus = newStatus;
        }
        if (myStatus != Presence.PRESENCE_OFFLINE) {
            lastOnlineStatus = myStatus;
        }
        if (myStatus != Presence.PRESENCE_OFFLINE && isDisconnected()) {
            connect();
            return;
        }
        if (isLoggedIn()) {
            final int priority = 0; // not dealing with priority
            final String message = null; // no text status
            Presence presence = new Presence(myStatus, priority, message, Setup.get().getSettings().getNick());
            xmppStream.send(presence);
        }
        if (myStatus == Presence.PRESENCE_OFFLINE) {
            disconnect();
        }
    }

    public void sendPresence(Presence presence) {
        final int newStatus = presence.getTypeIndex();
        if (newStatus != Presence.PRESENCE_SAME) {
            myStatus = newStatus;
        }
        final boolean goOffline = myStatus == Presence.PRESENCE_OFFLINE;
        if (!goOffline) {
            lastOnlineStatus = myStatus;
        }
        if (!goOffline && isDisconnected()) {
            connect();
            return;
        }
        if (isLoggedIn()) {
            // send presence
            MCLoggerFactory.getLogger(getClass()).debug("Send presence " + presence);
            xmppStream.send(presence);
        } else {
            MCLoggerFactory.getLogger(getClass()).debug("Not logged in, don't send: " + presence);
        }
        // disconnect
        if (goOffline) {
            disconnect();
        }
    }

    private boolean isDisconnected() {
        return xmppStream == null;
    }

    public boolean isLoggedIn() {
        return !isDisconnected() && xmppStream.loggedIn;
    }

    private Contact selfContact() {
        if (self == null) {
            self = new Contact(Setup.get().getSettings().getNick(), myJid.getJid(), Presence.PRESENCE_ONLINE, null);
        }
        return self;
    }

    private Contact serverContact() {
        if (server == null) {
            server = new Contact(Setup.get().getSettings().getServerNick(), Setup.get().getSettings().getServerJid(), Presence.PRESENCE_ONLINE, null);
        }
        return server;
    }

    public void loginFailed(String error) {
        if (Login.isUserLoggedIn() && Setup.get().getSettings().getIntProperty("reconnect.count", "3") < 0) {
            MCLoggerFactory.getLogger(getClass()).warn("Login failed, but keep reconnecting as of settings (" + error + ")");
        } else {
            dropConnectionTimeout();
            myStatus = Presence.PRESENCE_OFFLINE;
            MCLoggerFactory.getLogger(getClass()).warn("Login failed: " + error);
            disconnect();
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
        long timeout;
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
        xmppStream.addBlockListener(new Ping());
        xmppStream.addBlockListener(JabberRPC.getInstance());
        xmppStream.restartKeepAliveTask();
        xmppStream.loggedIn = true;
        reconnectCount = 0;
        isTryToLogin = false;
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
                }
            } else if (data instanceof Message) {
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
                int mType = Msg.MESSAGE_TYPE_IN;
                try { // type=null
                    if (type.equals("groupchat")) {
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
                    }
                    if (type.equals("headline")) {
                        mType = Msg.MESSAGE_TYPE_HEADLINE;
                    }
                } catch (Exception e) {
                    type = "chat";
                } //force type to chat
                Contact c = serverContact();
                if (c == null) {
                    return XMLBlockListener.BLOCK_REJECTED;
                } //not-in-list message dropped
                if (name == null) {
                    name = c.getName();
                }
                if (body != null) {
                    if (body.startsWith("/me ")) {
                        start_me = 3;
                    }
                    if (start_me >= 0) {
                        StringBuilder b = new StringBuilder("\01");
                        b.append(name).append("\02");
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
                if (body == null) {
                    return XMLBlockListener.BLOCK_REJECTED;
                }
                Msg m = new Msg(mType, from, subj, body);
                if (tStamp != 0) {
                    m.dateGmt = tStamp;
                }
                if (m.getBody().contains(SR.MS_IS_INVITING_YOU)) {
                    m.dateGmt = 0;
                }
                m.setHighlite(highlite);
                messageStore(c, m);
                return XMLBlockListener.BLOCK_PROCESSED;
            } else if (data instanceof Presence) {
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

    private void messageStore(Contact c, Msg message) {
        // could store messages here - not storing currently
        if (c == null) {
            return;
        }
        c.addMessage(message);
        if (!message.unread) {
            return;
        }
        if (message.messageType != Msg.MESSAGE_TYPE_HISTORY) {
            notifyMessage(message);
        }
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
        AuthFactory.createAuth();
    }

    public void connectionTerminated(final Exception e, long connectionId) {
        MCLoggerFactory.getLogger(getClass()).debug("Connection terminated: " + e.getMessage());
        synchronized (reconnectMutex) {
            if (reconnect) {
                MCLoggerFactory.getLogger(getClass()).debug("already reconnecting, continue");
            } else {
                dropConnectionTimeout();
                connectionListener.disconnected();
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
        int maxReconnect = Setup.get().getSettings().getIntProperty("reconnect.count", "3");
        if (isTryToLogin) {
            maxReconnect = Setup.get().getSettings().getIntProperty("first.login.reconnect.count", "2");
        }
        Setup.get().getSettings().switchHost();
        if (maxReconnect < 0) {
            reconnectCount++;
        } else if (reconnectCount >= maxReconnect) {
            reconnectCount = 0;
            if (!isTryToLogin && Setup.get().getUI().getDialogManager().confirmSafe(MobileApp.localize("connection.failed"), MobileApp.localize("connection.failed.reconnect"))) {
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

    public void loginMessage(String msg) {
    }

    private void setMyJid(Jid myJid) {
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