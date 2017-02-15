package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.rpc.xmpp.datablocks.Presence;

public class UserPressStatusExtender implements StatusExtender {

    private boolean userPress = false;

    public UserPressStatusExtender(boolean userPress) {
        this.userPress = userPress;
    }

    public void extend(Presence presence) {
        presence.addChildNs("x", "mc:userPress").setText(String.valueOf(userPress));
    }
}