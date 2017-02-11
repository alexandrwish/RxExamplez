package com.magenta.mc.client.android.rpc.xmpp;

import android.util.Log;

import com.magenta.mc.client.android.receiver.LoginCheckReceiver;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.util.UserUtils;
import com.magenta.mc.client.components.MCTimerTask;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xmpp.XMPPStream;

import java.io.IOException;
import java.sql.SQLException;

import okhttp3.OkHttpClient;

public class XMPPStream2 extends XMPPStream {

    private final LoginCheckReceiver receiver = new LoginCheckReceiver();
    private KeepAliveTask2 keepAlive;

    public XMPPStream2(String server, String host, int port, boolean ssl, long connectionId) throws IOException {
        super(server, host, port, ssl, connectionId);
        iostream = new Utf8IOStream(socket);
    }

    public static OkHttpClient getThreadSafeClient() {
        return new OkHttpClient();
    }

    public void restartKeepAliveTask() {
        stopKeepAliveTask();
        startKeepAliveTask();
    }

    private void stopKeepAliveTask() {
        if (keepAlive != null) {
            keepAlive.destroyTask();
            keepAlive = null;
        }
    }

    private void startKeepAliveTask() {
        pingSent = false;
        keepAlive = new KeepAliveTask2(Setup.get().getSettings().keepAlivePeriod(), Setup.get().getSettings().keepAliveType());
    }

    public void close() {
        stopKeepAliveTask();
        super.close();
    }

    public class KeepAliveTask2 extends MCTimerTask {

        long id;
        int type;

        KeepAliveTask2(int periodSeconds, int type) {
            this.type = type;
            Log.d("---dbg1", "periodSeconds " + periodSeconds);
            long periodRun = ((long) periodSeconds) * 1000; // milliseconds
            id = System.currentTimeMillis();
            MCLoggerFactory.getLogger(getClass()).debug("Scheduling Keep-Alive task: " + id + " (period: " + periodSeconds + ")");
            timer().schedule(this, periodRun, periodRun);
        }

        public void runTask() {
            final OkHttpClient client = getThreadSafeClient();
            if (Boolean.valueOf((String) Setup.get().getSettings().get(MxSettings.ENABLE_API))) {
                receiver.run(keepAlive, client);
            } else {
                try {
                    receiver.sendLocations(client, UserUtils.cutComponentName(Settings.get().getUserId()));
                } catch (IOException | SQLException e) {
                    MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                }
                tryToSend();
            }
        }

        public void tryToSend() {
            try {
                MCLoggerFactory.getLogger(getClass()).debug("Keep-Alive: " + id);
                sendKeepAlive(type);
            } catch (Exception e) {
                dispatcher.broadcastTerminatedConnection(e, connectionId);
                MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            }
            MCLoggerFactory.getLogger(getClass()).debug("Keep-Alive complete: " + id);
        }

        void destroyTask() {
            MCLoggerFactory.getLogger(getClass()).debug("Cancelling Keep-Alive: " + id);
            cancel();
        }
    }
}