package com.magenta.mc.client.android.rpc.xmpp;

import com.magenta.mc.client.android.mc.xml.XMLException;

/**
 * Created 19.01.2011
 *
 * @author Konstantin Pestrikov
 */
public class XmppErrorException extends XMLException {
    private XmppError error;

    public XmppErrorException(String text, XmppError error) {
        super(text);
        this.error = error;
    }

    /**
     * Creates a new instance of XMLException
     */
    public XmppErrorException(String text) {
        super(text);
    }

    public XmppError getError() {
        return error;
    }
}
