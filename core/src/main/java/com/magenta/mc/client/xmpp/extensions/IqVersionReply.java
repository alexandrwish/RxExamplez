/*
 * IqVersionReply.java
 *
 * Created on 27.02.2005, 18:31
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 *
 */

package com.magenta.mc.client.xmpp.extensions;

import com.magenta.mc.client.client.Msg;
import com.magenta.mc.client.client.XMPPClient;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.xml.XMLBlockListener;
import com.magenta.mc.client.xml.XMLDataBlock;
import com.magenta.mc.client.xmpp.datablocks.Iq;

/**
 * @author Eugene Stahov
 */
public class IqVersionReply implements XMLBlockListener {
    private final static String TOPFIELDS[] = {"name", "version", "os"};

    public IqVersionReply() {
    }

    // constructs version request

    public static XMLDataBlock query(String to) {
        XMLDataBlock result = new Iq(to, Iq.TYPE_GET, "getver");
        result.addChildNs("query", "jabber:iq:version");
        return result;
    }

    public int blockArrived(XMLDataBlock data) {
        if (!(data instanceof Iq)) {
            return BLOCK_REJECTED;
        }
        String type = data.getAttribute("type");
        if (type.equals("get")) {

            XMLDataBlock query = data.findNamespace("query", "jabber:iq:version");
            if (query == null) {
                return BLOCK_REJECTED;
            }

            Iq reply = new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
            //XMLDataBlock query=reply.addChildNs("query", "jabber:iq:version"); reuse existing request
            reply.addChild(query);
            query.addChild("name", Setup.get().getSettings().getAppName());
            query.addChild("version", Setup.get().getSettings().getAppVersion());

            XMPPClient.getInstance().send(reply);

            return XMLBlockListener.BLOCK_PROCESSED;
        }

        if (data.getAttribute("id").equals("getver")) {
            String body = null;
            if (type.equals("error")) {
                body = com.magenta.mc.client.locale.SR.MS_NO_VERSION_AVAILABLE;
            } else if (type.equals("result")) {
                XMLDataBlock vc = data.getChildBlock("query");
                if (vc != null) {
                    body = dispatchVersion(vc);
                }
            } else {
                return BLOCK_REJECTED;
            }

            if (body != null) {
                final XMPPClient roster = XMPPClient.getInstance();

                Msg m = new Msg(Msg.MESSAGE_TYPE_IN, "ver", com.magenta.mc.client.locale.SR.MS_CLIENT_INFO, body);

                //todo
                //roster.messageStore( roster.getContact( data.getAttribute("from"), false), m);
                roster.setQuerySign(false);
                //roster.redraw();
                return XMLBlockListener.BLOCK_PROCESSED;
            }
            //
        }
        return BLOCK_REJECTED;

    }

    private String dispatchVersion(XMLDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:version")) {
            return "unknown version namespace";
        }
        StringBuffer vc = new StringBuffer();
        //vc.append((char)0x01);
        for (int i = 0; i < TOPFIELDS.length; i++) {
            String field = data.getChildBlockText(TOPFIELDS[i]);
            if (field.length() > 0) {
                vc.append(TOPFIELDS[i])
                        .append((char) 0xa0)
                        .append(field)
                        .append('\n');
            }
        }
        return vc.toString();
    }
}
