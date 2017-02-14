/*
 * Captcha.java
 *
 * Created on 6 Май 2008 г., 1:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.magenta.mc.client.android.rpc.xmpp.extensions;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Message;
import com.magenta.mc.client.android.rpc.xmpp.extensions.dataforms.XDataForm;

/**
 * @author root
 */
public class Captcha implements XMLBlockListener, XDataForm.NotifyListener {

    private String from;
    private String id;

    /**
     * Creates a new instance of Captcha
     */
    public Captcha() {
    }

    public int blockArrived(XMLDataBlock data) {
        if (data instanceof Message) {

            XMLDataBlock challenge = data.findNamespace("captcha", "urn:xmpp:captcha");
            if (challenge == null) {
                return BLOCK_REJECTED;
            }

            XMLDataBlock xdata = challenge.findNamespace("x", "jabber:x:data");

            from = data.getAttribute("from");
            id = data.getAttribute("id");

            new XDataForm(xdata, this).fetchMediaElements(data.getChildBlocks());

            return BLOCK_PROCESSED;
        }

        if (data instanceof Iq) {
            if (!data.getAttribute("id").equals(id)) {
                return BLOCK_REJECTED;
            }

            //TODO: error handling
            //if ()
            return BLOCK_PROCESSED;
        }

        return BLOCK_REJECTED;
    }

    public void XDataFormSubmit(XMLDataBlock form) {
        XMLDataBlock reply = new Iq(from, Iq.TYPE_SET, id);
        reply.addChildNs("captcha", "urn:xmpp:captcha").addChild(form);

        XMPPClient.getInstance().send(reply);
    }

}
