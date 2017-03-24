package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.log.MCLoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 06.10.11
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
public class McDiagnosticAgent {

    public static final int DIAGNOSTIC_PORT = 9167;
    public static final String CREDENTIALS = "CREDS";
    public static final String PATH = "PATH";
    private static final char MSG_TERMINATOR = '#';
    private static final char VALUE_DELIMITER = '=';
    private static final String PING = "PING";
    private static final String PONG = "PONG";
    private static final String LOGOUT = "LOGOUT";
    private static final String QUIT = "QUIT";
    protected static McDiagnosticAgent instance;
    private boolean started;
    private boolean running = true;
    private boolean connected = false;
    private ServerSocket serverSocket;
    private LinkedQueue outbox;

    private Thread readThread;
    private Thread writeThread;
    private InputStream in;
    private OutputStream out;
    private Socket clientSocket;
    private boolean quitSent;

    protected McDiagnosticAgent() {
    }

    public static McDiagnosticAgent getInstance() {
        if (instance == null) {
            instance = new McDiagnosticAgent();
        }
        return instance;
    }

    public static void setInstance(McDiagnosticAgent ipc) {
        instance = ipc;
    }

    public void start() {
        started = true;

        MCLoggerFactory.getLogger(getClass()).info("Starting diagnostic socket: 127.0.0.1:" + DIAGNOSTIC_PORT);

        try {
            // try to capture a port to listen from diagnostic monitor
            serverSocket = new ServerSocket(DIAGNOSTIC_PORT);
        } catch (IOException e) {
            // another application instance might have captured the port, escaping then
            MCLoggerFactory.getLogger(getClass()).error("Error creating diagnostic server 127.0.0.1:" + DIAGNOSTIC_PORT);
            return;
        }

        outbox = new LinkedQueue();

        // run socket listener to let new app. instances know that already running
        readThread = new Thread() {
            public void run() {
                while (running) {
                    try {
                        clientSocket = serverSocket.accept();
                        connected = true;
                        MCLoggerFactory.getLogger(getClass()).info("Incoming diagnostic monitor, servicing connection");
                        in = clientSocket.getInputStream();
                        out = clientSocket.getOutputStream();
                        byte[] buf = new byte[128];

                        while (running) {
                            int length = in.read(buf);
                            if (length == 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    // wake up and check running status
                                }
                                continue;
                            }
                            for (int i = 0; i < length; i++) {
                                char nextChar = (char) buf[i];
                                if (MSG_TERMINATOR == nextChar) {
                                    String command = new String(buf, 0, i, "ASCII").trim();
                                    if (PING.equalsIgnoreCase(command)) {
                                        //MCLoggerFactory.getLogger(getClass()).trace("Incoming PING");
                                        try {
                                            outbox.put(PONG);
                                        } catch (InterruptedException e) {
                                            if (!running) // stop cycke if diagnostic stopped
                                                break;
                                        }
                                    }
                                }
                            }
                        }

                    } catch (IOException e) {
                        MCLoggerFactory.getLogger(getClass()).warn("read down, stopping agent", e);
                        McDiagnosticAgent.this.stop();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // ignore
                            }
                        }
                        if (clientSocket != null && !clientSocket.isClosed()) {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                // ignore
                            }
                        }
                    }
                }

            }
        };
        readThread.start();
        writeThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (running) {
                        try {
                            String msg = (String) outbox.poll(0);
                            if (msg != null && out != null) {
                                boolean quit = QUIT.equals(msg);
                                msg += MSG_TERMINATOR;
                                out.write(msg.getBytes());
                                if (quit) {
                                    out.flush();
                                    quitSent = true;
                                }
                            } else {
                                Thread.sleep(100);
                            }
                        } catch (InterruptedException e) {
                            // wake up
                        }
                    }
                } catch (IOException e) {
                    MCLoggerFactory.getLogger(getClass()).warn("write down, stopping agent", e);
                    stop();
                } finally {
                    quitSent = true;
                    try {
                        if (out != null)
                            out.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        });
        writeThread.start();
        MCLoggerFactory.getLogger(getClass()).info("Started");
    }

    public void stop() {
        started = false;
        if (running) {
            running = false;
            connected = false;
            MCLoggerFactory.getLogger(getClass()).info("Stoppng diagnostic socket: 127.0.0.1:" + DIAGNOSTIC_PORT);
            if (readThread != null) {
                readThread.interrupt();
                readThread = null;
            }
            if (writeThread != null) {
                writeThread.interrupt();
                writeThread = null;
            }
        }
    }

    public void signalQuit() {
        if (started) {
            try {
                MCLoggerFactory.getLogger(getClass()).info("Sending QUIT to diagnostic monitor");
                quitSent = false;
                outbox.put(QUIT);
                while (running && connected && !quitSent) {
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                // wake up
            }
        }
    }

    public void signalCredentials(String credentials) {
        if (started) {
            try {
                MCLoggerFactory.getLogger(getClass()).info("Sending CREDS to diagnostic monitor");
                outbox.put(CREDENTIALS + VALUE_DELIMITER + credentials);
            } catch (InterruptedException e) {
                // wake up
            }
        }
    }

    public void signalLogout() {
        if (started) {
            try {
                MCLoggerFactory.getLogger(getClass()).info("Sending LOGOUT to diagnostic monitor");
                outbox.put(LOGOUT);
            } catch (InterruptedException e) {
                // wake up
            }
        }
    }

    public void signalPath(String path) {
        if (started) {
            try {
                MCLoggerFactory.getLogger(getClass()).info("Sending PATH to diagnostic monitor");
                outbox.put(PATH + VALUE_DELIMITER + path);
            } catch (InterruptedException e) {
                // wake up
            }
        }
    }
}
