package com.magenta.mc.client.client;

import com.magenta.mc.client.components.MCTimerTask;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.setup.Setup;


/**
 * Created 01.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class Reconnect extends MCTimerTask {
    private final static long TIMEOUT = (long) Setup.get().getSettings().getIntProperty("reconnect.delay", "10") * 1000;
    private final static long FIRST_LOGIN_TIMEOUT =
            (long) Setup.get().getSettings().getIntProperty("first.login.reconnect.delay", "5") * 1000;

    public Reconnect(boolean immediately) {
        super();
        long reconnectDelay = TIMEOUT;
        if (XMPPClient.getInstance().isTryToLogin()) {
            reconnectDelay = FIRST_LOGIN_TIMEOUT;
        }
        MCLoggerFactory.getLogger(getClass()).debug("Scheduling reconnect with delay: " + reconnectDelay);
        timer().schedule(this, immediately ? 1 : reconnectDelay);
    }

    public void runTask() {
        XMPPClient.getInstance().doReconnect();
    }
}
