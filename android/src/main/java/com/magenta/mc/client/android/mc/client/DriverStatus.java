package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Presence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created 01.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class DriverStatus {
    public static final DriverStatus ONLINE = new DriverStatus("status.online", Presence.PRESENCE_ONLINE);
    public static final DriverStatus BREAK = new DriverStatus("status.break", Presence.PRESENCE_ONLINE);
    public static final DriverStatus GOING_HOME = new DriverStatus("status.going_home", Presence.PRESENCE_ONLINE);
    public static final DriverStatus UNAVAILABLE = new DriverStatus("status.unavailable", Presence.PRESENCE_ONLINE);
    public static final DriverStatus OFFLINE = new DriverStatus("status.offline", Presence.PRESENCE_OFFLINE);
    private static final String DRIVER_STATUS_PROPERTY = "driver.status";
    private static final Map statesByName = new HashMap();
    private static final DriverStatus[] states = new DriverStatus[]{
            ONLINE, BREAK, GOING_HOME, UNAVAILABLE, OFFLINE
    };
    private static DriverStatus current;
    private static DriverStatus lastOnline;

    static {
        for (int i = 0; i < states.length; i++) {
            DriverStatus state = states[i];
            statesByName.put(state.getName(), state);
        }
        initState();
    }

    private String name;
    private int xmppPresence;

    private DriverStatus(String name, int xmppPresence) {
        this.name = name;
        this.xmppPresence = xmppPresence;
    }

    /*
            load current status from settings
         */
    private static void initState() {
        String currentName;
        if (Setup.isInitialized()) {
            currentName = Setup.get().getSettings().getProperty(DRIVER_STATUS_PROPERTY, BREAK.getName());
        } else {
            currentName = OFFLINE.getName();
            MCLoggerFactory.getLogger(DriverStatus.class).warn("Setup is not initialized, set OFFLINE", new Exception());
        }
        DriverStatus.setCurrent(DriverStatus.byName(currentName));
    }

    public static DriverStatus byName(String name) {
        return (DriverStatus) statesByName.get(name);
    }

    public static DriverStatus getCurrent() {
        return current;
    }

    private static void setCurrent(DriverStatus status) {
        current = status;
        if (status.isOnline()) {
            setLastOnline(status);
        }
    }

    public static DriverStatus getLastOnline() {
        return lastOnline;
    }

    public static void setLastOnline(DriverStatus lastOnline) {
        DriverStatus.lastOnline = lastOnline;
    }

    /*
        save current status to settings
     */
    private static void saveState() {
        Setup.get().getSettings().setProperty(DRIVER_STATUS_PROPERTY, getLastOnline().getName());
        Setup.get().getSettings().saveSettings();
    }

    public static void resend() {
        setCurrent(getLastOnline());
        getCurrent().send(null);
    }

    protected Presence generatePresence(StatusExtender extender) {
        final int priority = 0; // not dealing with priority
        final String message = null; // no text status
        Presence presence = new Presence(getXmppPresence(), priority, message, null);
        presence.setFrom(Setup.get().getSettings().getJid());
        presence.setTo(Setup.get().getSettings().getServerComponentJid());
        presence.addChildNs("x", "mc:presence").setText(getName());
        if (extender != null) {
            extender.extend(presence);
        }
        return presence;
    }

    public boolean equals(Object o) {
        return o instanceof DriverStatus
                && getName().equalsIgnoreCase(((DriverStatus) o).getName());
    }

    public void set() {
        set(null);
    }

    public void set(StatusExtender extender) {
        set(true, extender);
    }

    public void saveCurrentState() {
        MCLoggerFactory.getLogger(getClass()).debug("Set " + getName());
        DriverStatus.setCurrent(this);
        DriverStatus.saveState();
    }

    public void set(boolean sendIfNotConnected, StatusExtender extender) {
        saveCurrentState();
        if (!ConnectionListener.getInstance().isConnected() && !sendIfNotConnected) {
            return;
        }
        send(extender);
    }

    private void send(StatusExtender extender) {
        Presence presence = generatePresence(extender);
        sendPresence(presence);
    }

    private void sendPresence(Presence presence) {
        XMPPClient.getInstance().sendPresence(presence);
    }

    public String getName() {
        return name;
    }

    public int getXmppPresence() {
        return xmppPresence;
    }

    public boolean isOnline() {
        return Presence.PRESENCE_OFFLINE != xmppPresence;
    }
}
