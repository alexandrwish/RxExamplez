/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.magenta.mc.client.android.mc.xmpp;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.mc.client.Login;
import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.exception.OperationTimeoutException;
import com.magenta.mc.client.android.mc.io.Utf8IOStream;
import com.magenta.mc.client.android.mc.locale.SR;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.login.SASLAuth;
import com.magenta.mc.client.android.mc.settings.PropertyEventListener;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.mc.xml.XMLException;
import com.magenta.mc.client.android.mc.xml.XMLParser;
import com.magenta.mc.client.android.mc.xmpp.datablocks.Iq;
import com.magenta.mc.client.android.mc.xmpp.datablocks.Presence;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.SSLSocketFactory;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;


/**
 * The stream to a jabber server.
 */

public class XMPPStream extends XmppParser implements Runnable {

    private static int readerThreadCount = 1;
    private final Object connectionTimeoutMutex = new Object();
    public boolean pingSent;
    public boolean loggedIn;
    protected Utf8IOStream iostream;
    protected Socket socket;
    protected XMPPDataBlockDispatcher dispatcher;
    protected long connectionId;
    private boolean xmppV1;
    private String sessionId;
    private String server; // for ping
    private PropertyEventListener settingsListener;
    // a thread pool with unbounded queue and single thread
    // to process outgoing packets
    private PooledExecutor outboxProcessor;
    private boolean connectionTimedOut = false;

    private boolean closed = false;
    private long pingMillis;
    private KeepAliveTask keepAlive;

    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     */
    public XMPPStream(String server, String host, int port, boolean ssl, final long connectionId)
            throws IOException {
        this.server = server;
        this.connectionId = connectionId;

        try {
            settingsListener = new PropertyEventListener() {
                public void propertyChanged(String property, String oldValue, String newValue) {
                    if (Settings.KEEP_ALIVE_PERIOD.equals(property)
                            || Settings.KEEP_ALIVE_TYPE.equals(property)) {
                        restartKeepAliveTask();
                    } else if (Settings.HOST.equals(property)
                            || Settings.PORT.equals(property)) {
                        Setup.get().getUI().getDialogManager().asyncMessageSafe(
                                MobileApp.localize("Note"),
                                MobileApp.localize("msg.connection_settings_changed"));
                    }
                }
            };

            Setup.get().getSettings().addPropertyListener(settingsListener);

            // initialize internet connection in Windows Mobile
            Setup.get().getPlatformUtil().startConnection();

            /*if (Setup.get().getSettings().getBooleanProperty("use.microedition.connector", "false")) {
                // using microedition Connector
                StringBuffer url=new StringBuffer();
                url.append(host).append(':').append(port);
                url.insert(0, (ssl)?"ssl://":"socket://");
                final StreamConnection connection = (StreamConnection) ConnectorFactory.open(url.toString());
                iostream = new Utf8IOStream(connection);
            } else {*/
            // using sockets
            if (ssl) {
                socket = SSLSocketFactory.getDefault().createSocket(/*host, port*/);
            } else {
                socket = new Socket(/*host, port*/);
            }
            socket.setSoTimeout(10000);
            MCLoggerFactory.getLogger(getClass()).debug("Connecting to " + host + ":" + port);
            socket.connect(new InetSocketAddress(host, port), 10000);
            MCLoggerFactory.getLogger(getClass()).debug("connected");
            iostream = new Utf8IOStream(socket);
            //}

            outboxProcessor = new PooledExecutor(new LinkedQueue(), 1);
            outboxProcessor.setKeepAliveTime(-1);
            outboxProcessor.setThreadFactory(new ThreadFactory() {
                public Thread newThread(Runnable command) {
                    return new Thread(command, "Outbox-" + connectionId);
                }
            });

            MCLoggerFactory.getLogger(getClass()).debug("Creating XMPPDataBlockDispatcher...");
            dispatcher = new XMPPDataBlockDispatcher(this);
            MCLoggerFactory.getLogger(getClass()).debug("Created, Starting XMPPStream reader thread");
            new Thread(this, "XMPPStream-" + readerThreadCount++).start();
            MCLoggerFactory.getLogger(getClass()).debug("started");
        } catch (IOException e) {
            handleStartupException(e);
        } catch (RuntimeException e) {
            handleStartupException(e);
        }

    }

    private void handleStartupException(RuntimeException e) {
        safeClose();
        throw e;
    }

    private void handleStartupException(IOException e) throws IOException {
        safeClose();
        throw e;
    }

    private void safeClose() {
        try {
            close();
        } catch (Exception e2) {
            // unlikely exception here
        }
    }

    public void initiateStream() throws IOException {
        if (Setup.get().getSettings().getBooleanProperty("session.quickstart", "false")) {
            StringBuffer header = new StringBuffer("<stream:quickstart to='")
                    .append(server)
                    .append("' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'");


            header.append(" sasl-plain='").append(SASLAuth.plainAuthText()).append("'");

            if (Setup.get().getSettings().useCompression()) {
                header.append(" compress='zlib'");
            }
            header.append(" bind-resource='").append(Setup.get().getSettings().getResource()).append("'");
            header.append(" start-session='true'");

            header.append('>');
            xmppV1 = true;
            dispatcher.broadcastBeginConversation();
            sendFirstOrder(header.toString());
            if (Setup.get().getSettings().useCompression()) {
                setZlibCompression();
            }
        } else {
            StringBuffer header = new StringBuffer("<stream:stream to='")
                    .append(server)
                    .append("' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'");

            if (SR.MS_XMLLANG != null) {
                header.append(" xml:lang='").append(SR.MS_XMLLANG).append("'");
            }
            header.append('>');
            sendFirstOrder(header.toString());
        }
    }

    public boolean tagStart(String name, Vector attributes) {
        if (name.equals("stream:stream")) {
            //if (name.equals("stream")) {
            sessionId = XMLParser.extractAttribute("id", attributes);
            String version = XMLParser.extractAttribute("version", attributes);
            xmppV1 = ("1.0".equals(version));

            dispatcher.broadcastBeginConversation();
            return false;
        }

        return super.tagStart(name, attributes);
    }

    public void tagEnd(String name) throws XMLException {
        if (currentBlock == null) {
            if (name.equals("stream:stream")) {
                dispatcher.halt();
                iostream.close(false);
                throw new XMLException("Normal stream shutdown");
            }
            return;
        }

        if (currentBlock.getParent() == null) {

            if (currentBlock.getTagName().equals("stream:error")) {
                XmppError xe = XmppError.decodeStreamError(currentBlock);

                dispatcher.halt();
                iostream.close(false);
                throw new XmppErrorException("Stream error: " + xe.toString(), xe);
            }
        }

        super.tagEnd(name);
    }

    protected void dispatchXmppStanza(XMLDataBlock currentBlock) {
        MCLoggerFactory.getLogger(getClass()).debug("received block: ");
        MCLoggerFactory.getLogger(getClass()).debug(currentBlock.toString());
        dispatcher.broadcastXmlDataBlock(currentBlock);
    }

    public void pingResponce() {
        final long pingLatency = System.currentTimeMillis() - pingMillis;
        MCLoggerFactory.getLogger(XMPPStream.class).debug("Ping latency: " + pingLatency + "ms");
        pingSent = false;
    }

    private void startKeepAliveTask() {
        pingSent = false;
        keepAlive = new KeepAliveTask(
                Setup.get().getSettings().keepAlivePeriod(),
                Setup.get().getSettings().keepAliveType());
    }

    private void stopKeepAliveTask() {
        if (keepAlive != null) {
            keepAlive.destroyTask();
            keepAlive = null;
        }
    }

    public void restartKeepAliveTask() {
        stopKeepAliveTask();
        startKeepAliveTask();
    }

    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    public void run() {
        try {
            XMLParser parser = new XMLParser(this);

            byte cbuf[] = new byte[512];

            while (!closed) {
                int length = iostream.read(cbuf);

                if (length == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                    continue;
                }

                parser.parse(cbuf, length);
            }

        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).debug("Exception in parser:", e);
            dispatcher.broadcastTerminatedConnection(e, connectionId);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Method to close the connection to the server and tell the listener
     * that the connection has been terminated.
     */

    public void close() {
        if (outboxProcessor != null) {
            outboxProcessor.shutdownNow();
        }
        stopKeepAliveTask();
        Setup.get().getSettings().removePropertyListener(settingsListener);

        if (dispatcher != null) {
            dispatcher.setJabberListener(null);
        }
        try {
            //TODO: see FS#528
            try {
                //Thread.sleep(500);
                Thread.sleep(50);
            } catch (Exception e) {
                //ok
            }
            sendFirstOrder("</stream:stream>");
            int time = 10;
            while (dispatcher != null && dispatcher.isActive()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    //ok
                }
                if ((--time) < 0) {
                    break;
                }
            }
        } catch (IOException e) {
            // Ignore an IO Exceptions because they mean that the stream is
            // unavailable, which is irrelevant.
        } finally {
            closed = true;
            try {
                if (dispatcher != null) {
                    dispatcher.halt();
                }
                if (iostream != null) {
                    System.out.println("closing: " + Login.isLogout());
                    iostream.close(Login.isLogout());
                    System.out.println("closed");
                }
            } finally {
                Setup.get().getPlatformUtil().closeConnection();
            }
        }
    }

    /**
     * Method of sending data to the server.
     *
     * @param type ping type : 1 - space, 2 - </iq>, 3 - xmpp ping
     */
    public void sendKeepAlive(int type) throws IOException {
        switch (type) {
            case 3:
                if (pingSent) {
                    dispatcher.broadcastTerminatedConnection(new Exception("Ping Timeout"), connectionId);
                } else {
                    ping();
                }
                break;
            case 2:
                sendFirstOrder("<iq/>");
                break;
            case 1:
                sendFirstOrder(" ");
        }
    }

    /*
        Using this to send out-of-order packets.
        Main queue (at outboxProcessor) will wait then
     */
    public void sendFirstOrder(final String data) throws IOException {
        sendBufferWithTimeout(new StringBuffer(data));
    }

    private void sendBufferWithTimeout(final StringBuffer data) throws IOException {
        final List exception = new ArrayList();

        Runnable abort = new Runnable() {

            public void run() {
                iostream.closeSocket();
            }
        };

        try {
            MobileApp.runAbortableTaskWithTimeout(new Runnable() {
                public void run() {
                    try {
                        sendBuffer(data);
                    } catch (Exception e) {
                        exception.add(e);
                        if (e instanceof OperationTimeoutException) {
                            synchronized (connectionTimeoutMutex) {
                                if (!connectionTimedOut) {
                                    connectionTimedOut = true;
                                }
                            }
                        }
                    }
                }
            }, abort, Setup.get().getSettings().getIntProperty("connection.timeout", "10000"));
        } catch (Exception e) {
            exception.add(e);
        }

        if (exception.size() > 0) {
            final Object exc = exception.get(0);
            if (exc instanceof IOException) {
                throw (IOException) exc;
            } else {
                throw (RuntimeException) exc;
            }
        }
    }

    private void sendBuffer(final StringBuffer data) throws IOException {
        iostream.send(data);
    }

    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */

    public void send(XMLDataBlock block) {
        new SendDataBlockTask(block).start();
    }

    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */

    public void send(XMLDataBlock block, boolean notifyFailure) {
        new SendDataBlockTask(block, notifyFailure).start();
    }

    public void send(Presence presence) {
        try {
            sendFirstOrder(presence.toString());
        } catch (IOException e) {
            dispatcher.broadcastTerminatedConnection(e, connectionId);
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).error("Error while sending presence " + presence, e);
        }
    }

    /**
     * Set the listener to this stream.
     */

    public void addBlockListener(XMLBlockListener listener) {
        dispatcher.addBlockListener(listener);
    }

    public void cancelBlockListener(XMLBlockListener listener) {
        dispatcher.cancelBlockListener(listener);
    }

    public void cancelBlockListenerByClass(Class removeClass) {
        dispatcher.cancelBlockListenerByClass(removeClass);
    }

    public void setJabberListener(XMPPListener listener) {
        dispatcher.setJabberListener(listener);
    }

    private void ping() {
        XMLDataBlock ping = new Iq(Setup.get().getSettings().getServerJid(), Iq.TYPE_GET, "ping");
        ping.addChildNs("ping", "urn:xmpp:ping");

        pingMillis = System.currentTimeMillis();
        pingSent = true;
        try {
            sendFirstOrder(ping.toString());
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).error("Error while sending ping ", e);
        }
    }

    public boolean isXmppV1() {
        return xmppV1;
    }


    //#if ZLIB

    public String getSessionId() {
        return sessionId;
    }

    public void setZlibCompression() {
        iostream.setStreamCompression();
    }
//#endif

    public String getStreamStats() {
        return iostream.getStreamStats();
    }

    private class KeepAliveTask extends MCTimerTask {
        private long id;
        private int type;

        public KeepAliveTask(int periodSeconds, int type) {
            this.type = type;
            long periodRun = ((long) periodSeconds) * 1000; // milliseconds
            id = System.currentTimeMillis();
            MCLoggerFactory.getLogger(getClass()).debug("Scheduling Keep-Alive task: " + id + " (period: " + periodSeconds + ")");
            timer().schedule(this, periodRun, periodRun);
        }

        public void runTask() {
            try {
                MCLoggerFactory.getLogger(getClass()).debug("Keep-Alive: " + id);
                sendKeepAlive(type);
            } catch (Exception e) {
                dispatcher.broadcastTerminatedConnection(e, connectionId);
                e.printStackTrace();
            }
            MCLoggerFactory.getLogger(getClass()).debug("Keep-Alive complete: " + id);
        }

        public void destroyTask() {
            MCLoggerFactory.getLogger(getClass()).debug("cancelling Keep-Alive: " + id);
            this.cancel();
        }
    }

    private class PacketTimeout extends MCTimerTask {
        public void runTask() {
            boolean notify = false;
            synchronized (connectionTimeoutMutex) {
                if (!connectionTimedOut) {
                    notify = true;
                    connectionTimedOut = true;
                }
            }
            if (notify) {
                dispatcher.broadcastTerminatedConnection(new RuntimeException("Packet Timeout"), connectionId);
            }
        }
    }

    /*
        Sends XML block using outboxProcessor queued executor
         and 'connection.timeout'
     */
    private class SendDataBlockTask implements Runnable {
        private XMLDataBlock data;
        private boolean notifyFailure = true;
        private PacketTimeout packetTimeout = new PacketTimeout();

        public SendDataBlockTask(XMLDataBlock data) {
            this.data = data;
        }

        public SendDataBlockTask(XMLDataBlock data, boolean notifyFailure) {
            this.data = data;
            this.notifyFailure = notifyFailure;
        }

        public void start() {
            try {
                outboxProcessor.execute(this);
            } catch (InterruptedException e) {
                // die
            }
        }

        public void run() {
            if (!connectionTimedOut) {
                boolean canceled = false;
                try {
                    StringBuffer buf = new StringBuffer();
                    data.constructXML(buf);
                    MobileApp.getInstance().getTimer().schedule(packetTimeout, Setup.get().getSettings().getIntProperty("connection.timeout", "10000"));
                    sendBuffer(buf);
                } catch (Exception e) {
                    packetTimeout.cancel();
                    canceled = true;
                    // probably connection has broken
                    e.printStackTrace();

                    if (notifyFailure) {
                        dispatcher.broadcastTerminatedConnection(e, connectionId);
                    } else {
                        MCLoggerFactory.getLogger(getClass()).debug("failure notification supressed, ignoring exception");
                    }
                }
                if (!canceled) {
                    packetTimeout.cancel();
                }
            }
        }
    }
}
