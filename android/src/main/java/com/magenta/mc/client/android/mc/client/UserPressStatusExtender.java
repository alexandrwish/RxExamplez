package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.rpc.xmpp.datablocks.Presence;

/**
 * Created with IntelliJ IDEA.
 * User: aosipov
 * Date: 01.10.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class UserPressStatusExtender implements StatusExtender {

    private boolean userPress = false;

    public UserPressStatusExtender() {
    }

    public UserPressStatusExtender(boolean userPress) {
        this.userPress = userPress;
    }

    public void extend(Presence presence) {
        presence.addChildNs("x", "mc:userPress").setText(String.valueOf(userPress));
    }
}
