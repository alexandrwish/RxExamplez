package com.magenta.mc.client.android.mc.login;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.rpc.xmpp.XMPPStream;

import java.util.ArrayList;
import java.util.List;

public class AuthFactory {

    private static List<LoginListener> listeners = new ArrayList<>();

    private static AuthProvider provider = new AuthProvider() {

        public XMLBlockListener getAuth() {
            final XMPPClient client = XMPPClient.getInstance();
            final XMPPStream stream = client.getXmppStream();
            if (stream.isXmppV1()) {
                return new SASLAuth(allListeners(), stream);
            } else {
                return new NonSASLAuth(allListeners(), stream);
            }
        }
    };

    private static LoginListenerWrapper allListeners() {
        LoginListener[] res = new LoginListener[listeners.size() + 1];
        res[0] = XMPPClient.getInstance();
        int i = 1;
        for (Object listener : listeners) {
            res[i++] = (LoginListener) listener;
        }
        return new LoginListenerWrapper(res);
    }

    public static void addListener(LoginListener listener) {
        listeners.add(listener);
    }

    public static List<LoginListener> getListeners() {
        return listeners;
    }

    public static XMLBlockListener createAuth() {
        return provider.getAuth();
    }

    public interface AuthProvider {

        XMLBlockListener getAuth();
    }
}