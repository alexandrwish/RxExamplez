package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.util.Time;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;
import com.magenta.mc.client.android.rpc.extensions.XMPPTimeResponse;

public class TimeSynchronization implements XMPPTimeResponse.TimeListener {

    private static long timeRequestSent;

    public static void synchronize() {
        Time.init();
        final Iq iq = new Iq(Setup.get().getSettings().getServerJid(), Iq.TYPE_GET, "123");
        iq.addChildNs("time", "urn:xmpp:time");
        XMPPTimeResponse.setListener(new TimeSynchronization());
        timeRequestSent = System.currentTimeMillis();
        XMPPClient.getInstance().send(iq);
    }

    public void gotTime(long serverTime, int tzoHours) {
        final long currTime = System.currentTimeMillis();
        final long doubleWayTime = currTime - timeRequestSent;
        long timeDelta = serverTime - currTime - doubleWayTime / 2;
        Setup.get().getSettings().setTimeDelta(timeDelta);
        Setup.get().getSettings().setServerTZOffset(tzoHours);
    }
}