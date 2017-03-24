package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.setup.Setup;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MobileCentralIPC {

    public static final String CURRENT_FRAME_TO_FRONT_COMMAND = "CURRENT_FRAME_TO_FRONT_COMMAND";
    public static final String EXIT_APP_COMMAND = "EXIT_APP_COMMAND";

    public static final int IPC_PORT = 9157;
    protected static MobileCentralIPC instance;
    private boolean ipcRunning = true;
    private ServerSocket serverSocket;

    protected MobileCentralIPC() {
    }

    public static MobileCentralIPC getInstance() {
        if (instance == null) {
            instance = new MobileCentralIPC();
        }
        return instance;
    }

    public static void setInstance(MobileCentralIPC ipc) {
        instance = ipc;
    }

    public void start() {
        try {
            // try establishing socket connection to check if another application instance exists
            // this is a second instance, suicide then
            new Socket("127.0.0.1", IPC_PORT).getOutputStream().write(CURRENT_FRAME_TO_FRONT_COMMAND.getBytes("ASCII"));
            MCLoggerFactory.getLogger(getClass()).warn("Another running instance detected, send CURRENT_FRAME_TO_FRONT_COMMAND and close app");
            System.exit(0);
        } catch (IOException e) {
            MCLoggerFactory.getLogger(getClass()).debug("Threre no another application instances");
        }

        try {
            // try to capture a port to listen from other application instances
            serverSocket = new ServerSocket(IPC_PORT);
        } catch (IOException e) {
            // another application instance might have captured the port, escaping then
            MCLoggerFactory.getLogger(getClass()).error("Error creating instance detection server, closing application due to conflict");
            System.exit(0);
        }

        // run socket listener to let new app. instances know that already running
        new Thread() {
            public void run() {
                while (ipcRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        InputStream in = clientSocket.getInputStream();
                        byte[] buf = new byte[255];
                        int readed = in.read(buf);
                        String command = new String(buf, 0, readed, "ASCII").trim();
                        if (EXIT_APP_COMMAND.equals(command)) {
                            onExitCommand();
                        } else if (CURRENT_FRAME_TO_FRONT_COMMAND.equals(command)) {
                            MCLoggerFactory.getLogger(getClass()).info("got CURRENT_FRAME_TO_FRONT_COMMAND");
                            Setup.get().getUI().toFront();
                        } else {
                            MCLoggerFactory.getLogger(getClass()).warn("got unknown command " + command);
                        }
                        clientSocket.close();
                    } catch (IOException e) {
                        MCLoggerFactory.getLogger(getClass()).warn("IOException in IPC thread", e);
                    }
                }

            }
        }.start();
        MCLoggerFactory.getLogger(getClass()).info("Started");
    }

    public void stop() {
        ipcRunning = false;
    }

    public void onExitCommand() {
        MCLoggerFactory.getLogger(getClass()).info("got EXIT_APP_COMMAND");
        MobileApp.getInstance().exit();
    }
}