package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.mc.client.resend.Resender;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

/**
 * Created 30.04.2010
 *
 * @author Konstantin Pestrikov
 */
public class ConnectionListener {
    private static boolean connecting;
    private static ConnectionListener instance;
    private boolean connected;
    private Listener listener = new Listener() {
        public void connected() {
            MCLoggerFactory.getLogger(ConnectionListener.class).debug("Connected to server");
        }

        public void disconnected() {
            MCLoggerFactory.getLogger(ConnectionListener.class).debug("Disconnected from server");
        }
    };

    private ConnectionListener() {

    }

    public static ConnectionListener getInstance() {
        if (instance == null) {
            instance = new ConnectionListener();
        }
        return instance;
    }

    public static void startConnecting() {
        connecting = true;
    }

    public static void stopConnecting() {
        connecting = false;
        Login.wake();
    }

    public static boolean isConnecting() {
        return connecting;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void connected() {
        connecting = false;
        connected = true;
        DriverStatus.resend();
        Resender.getInstance().start();
        notifyConnected();
    }

    private void notifyConnected() {
        if (listener != null) {
            listener.connected();
        }
    }

    public void disconnected() {
        try {
            connected = false;
            DriverStatus.OFFLINE.set(false, null);
            notifyDisconnected();
            Resender.getInstance().stop();
        } catch (Exception e) {
            MCLoggerFactory.getLogger(getClass()).debug(e.getStackTrace());
        }
    }

    private void notifyDisconnected() {
        if (listener != null) {
            listener.disconnected();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public interface Listener {
        void connected();

        void disconnected();
    }
}
