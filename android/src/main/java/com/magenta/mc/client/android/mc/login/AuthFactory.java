package com.magenta.mc.client.android.mc.login;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.rpc.xmpp.XMPPStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created 02.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class AuthFactory {
    private static List listeners = new ArrayList();

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

    public static LoginListenerWrapper allListeners() {
        LoginListener[] res = new LoginListener[listeners.size() + 1];
        res[0] = XMPPClient.getInstance();
        int i = 1;
        for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
            res[i] = (LoginListener) iterator.next();
            i++;
        }
        return new LoginListenerWrapper(res);
    }

    public static void useProvider(AuthProvider provider) {
        AuthFactory.provider = provider;
    }

    public static void addListener(LoginListener listener) {
        listeners.add(listener);
    }

    public static List getListeners() {
        return listeners;
    }

    public static XMLBlockListener createAuth() {
        return provider.getAuth();
    }

    public interface AuthProvider {
        XMLBlockListener getAuth();
    }
}
