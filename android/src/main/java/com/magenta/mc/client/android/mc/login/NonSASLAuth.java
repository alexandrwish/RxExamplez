/*
 * NonSASLAuth.java
 *
 * Created on 8.06.2006, 22:16
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.magenta.mc.client.android.mc.login;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.crypto.SHA1;
import com.magenta.mc.client.android.mc.locale.SR;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.util.Strconv;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.XMPPStream;
import com.magenta.mc.client.android.rpc.xmpp.XmppError;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;

/**
 * @author evgs
 */
public class NonSASLAuth implements XMLBlockListener {

    private final static int AUTH_GET = 0;
    private final static int AUTH_PASSWORD = 1;
    private final static int AUTH_DIGEST = 2;
    private LoginListener listener;
    private XMPPStream stream;

    /**
     * Creates a new instance of NonSASLAuth
     */
    public NonSASLAuth(LoginListener listener, XMPPStream stream) {
        this.listener = listener;
        this.stream = stream;

        stream.addBlockListener(this);

        jabberIqAuth(AUTH_GET);

        listener.loginMessage(SR.MS_AUTH);
    }

    private void jabberIqAuth(int authType) {
        int type = Iq.TYPE_GET;
        String id = "auth-1";

        XMLDataBlock query = new XMLDataBlock("query", null, null);
        query.setNameSpace("jabber:iq:auth");
        query.addChild("username", Setup.get().getSettings().getLogin());

        switch (authType) {
            case AUTH_DIGEST:
                SHA1 sha = new SHA1();
                sha.init();
                sha.updateASCII(stream.getSessionId());
                sha.updateASCII(Strconv.unicodeToUTF(Setup.get().getSettings().getPassword()));
                sha.finish();
                query.addChild("digest", sha.getDigestHex());

                query.addChild("resource", Setup.get().getSettings().getResource());
                type = Iq.TYPE_SET;
                id = "auth-s";
                break;

            case AUTH_PASSWORD:
                query.addChild("password", Setup.get().getSettings().getPassword());
                query.addChild("resource", Setup.get().getSettings().getResource());
                type = Iq.TYPE_SET;
                id = "auth-s";
                break;
        }


        Iq auth = new Iq(Setup.get().getSettings().getServerName(), type, id);
        auth.addChild(query);

        stream.send(auth);
    }

    public int blockArrived(XMLDataBlock data) {
        try {
            if (data instanceof Iq) {
                String type = data.getTypeAttribute();
                String id = data.getAttribute("id");
                if (id.equals("auth-s")) {
                    if (type.equals("error")) {
                        // Authorization error
                        listener.loginFailed(XmppError.findInStanza(data).toString());

                        return XMLBlockListener.NO_MORE_BLOCKS;
                    } else if (type.equals("result")) {
                        listener.loginSuccess(XMPPClient.getInstance().isTryToLogin());
                        return XMLBlockListener.NO_MORE_BLOCKS;
                    }
                }
                if (id.equals("auth-1")) {
                    try {
                        XMLDataBlock query = data.getChildBlock("query");

                        if (query.getChildBlock("digest") != null) {
                            jabberIqAuth(AUTH_DIGEST);
                            return XMLBlockListener.BLOCK_PROCESSED;
                        }

                        if (query.getChildBlock("password") != null) {
                            if (!Setup.get().getSettings().getPlainAuth()) {
                                listener.loginFailed("Plain auth required");
                                return XMLBlockListener.NO_MORE_BLOCKS;
                            }
                            jabberIqAuth(AUTH_PASSWORD);
                            return XMLBlockListener.BLOCK_PROCESSED;
                        }

                        listener.loginFailed("Unknown mechanism");

                    } catch (Exception e) {
                        listener.loginFailed(e.toString());
                    }
                    return XMLBlockListener.NO_MORE_BLOCKS;
                }
            }

        } catch (Exception e) {
        }
        return XMLBlockListener.BLOCK_REJECTED;
    }

}
