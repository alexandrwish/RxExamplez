/*
 * IqTimeReply.java
 *
 * Created on 10.09.2005, 23:15
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
 */

package com.magenta.mc.client.android.rpc.xmpp.extensions;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.util.Time;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;

/**
 * @author EvgS
 */
public class IqTimeReply implements XMLBlockListener {

    public IqTimeReply() {
    }

    public int blockArrived(XMLDataBlock data) {
        if (!(data instanceof Iq)) {
            return BLOCK_REJECTED;
        }
        if (!data.getAttribute("type").equals("get")) {
            return BLOCK_REJECTED;
        }

        XMLDataBlock query = data.findNamespace("query", "jabber:iq:time");
        if (query != null) {
            query.addChild("utc", Time.Xep0082UtcTime());
            query.addChild("display", Time.dispLocalTime());
        } else {
            query = data.findNamespace("time", "urn:xmpp:time");
            if (query == null) {
                return BLOCK_REJECTED;
            }
            query.addChild("utc", Time.utcTime());
            query.addChild("tzo", Time.tzOffset());
        }

        Iq reply = new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        reply.addChild(query);

        XMPPClient.getInstance().send(reply);

        return XMLBlockListener.BLOCK_PROCESSED;

    }
}
