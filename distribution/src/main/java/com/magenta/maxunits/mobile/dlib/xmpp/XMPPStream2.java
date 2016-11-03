package com.magenta.maxunits.mobile.dlib.xmpp;

import android.util.Log;

import com.magenta.maxunits.mobile.dlib.receiver.LoginCheckReceiver;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.utils.UserUtils;
import com.magenta.mc.client.components.MCTimerTask;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xmpp.XMPPStream;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.sql.SQLException;

public class XMPPStream2 extends XMPPStream {

    final LoginCheckReceiver receiver = new LoginCheckReceiver();
    KeepAliveTask2 keepAlive;

    public XMPPStream2(String server, String host, int port, boolean ssl, long connectionId) throws IOException {
        super(server, host, port, ssl, connectionId);
        iostream = new Utf8IOStream(socket);
    }

    public static DefaultHttpClient getThreadSafeClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
        return client;

    }

    public void restartKeepAliveTask() {
        stopKeepAliveTask();
        startKeepAliveTask();
    }

    void stopKeepAliveTask() {
        if (keepAlive != null) {
            keepAlive.destroyTask();
            keepAlive = null;
        }
    }

    void startKeepAliveTask() {
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

        public KeepAliveTask2(int periodSeconds, int type) {
            this.type = type;
            Log.d("---dbg1", "periodSeconds " + periodSeconds);
            long periodRun = ((long) periodSeconds) * 1000; // milliseconds
            id = System.currentTimeMillis();
            MCLoggerFactory.getLogger(getClass()).debug("Scheduling Keep-Alive task: " + id + " (period: " + periodSeconds + ")");
            timer().schedule(this, periodRun, periodRun);
        }

        public void runTask() {
            final HttpClient client = getThreadSafeClient();
            if (Boolean.valueOf((String) Setup.get().getSettings().get(MxSettings.ENABLE_API))) {
                receiver.run(keepAlive, client);
            } else {
                try {
                    receiver.sendLocations(client, UserUtils.cutComponentName(Settings.get().getUserId()));
                } catch (IOException e) {
                    MCLoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                } catch (SQLException e) {
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

        public void destroyTask() {
            MCLoggerFactory.getLogger(getClass()).debug("Cancelling Keep-Alive: " + id);
            cancel();
        }
    }
}