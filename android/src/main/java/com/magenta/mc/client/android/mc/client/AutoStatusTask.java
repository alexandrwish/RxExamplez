package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.xmpp.datablocks.Presence;

/**
 * Created 01.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class AutoStatusTask extends MCTimerTask {

    public AutoStatusTask() {
        start();
    }

    private void start() {
        timer().schedule(this, Setup.get().getSettings().getAutoAwayDelay());
    }

    public void restart() {
        cancel();
        start();
    }

    public void runTask() {
        XMPPClient.getInstance().setAutoStatus(Presence.PRESENCE_AWAY);
    }
}
