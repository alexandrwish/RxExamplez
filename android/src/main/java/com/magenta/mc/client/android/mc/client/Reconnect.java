package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;

public class Reconnect extends MCTimerTask {

    private final static long TIMEOUT = (long) Setup.get().getSettings().getIntProperty("reconnect.delay", "10") * 1000;
    private final static long FIRST_LOGIN_TIMEOUT = (long) Setup.get().getSettings().getIntProperty("first.login.reconnect.delay", "5") * 1000;

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