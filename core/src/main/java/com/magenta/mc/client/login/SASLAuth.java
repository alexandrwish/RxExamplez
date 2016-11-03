/*
 * SASLAuth.java
 *
 * Created on 8.06.2006, 23:34
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

package com.magenta.mc.client.login;

import com.magenta.mc.client.client.XMPPClient;
import com.magenta.mc.client.crypto.MD5;
import com.magenta.mc.client.locale.SR;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.Strconv;
import com.magenta.mc.client.xml.XMLBlockListener;
import com.magenta.mc.client.xml.XMLDataBlock;
import com.magenta.mc.client.xmpp.XMPPStream;
import com.magenta.mc.client.xmpp.XmppError;
import com.magenta.mc.client.xmpp.datablocks.Iq;

import java.io.IOException;

/**
 * @author evgs
 */
public class SASLAuth implements XMLBlockListener {

    private LoginListener listener;
    private XMPPStream stream;

    /**
     * Creates a new instance of SASLAuth
     */
    public SASLAuth(LoginListener listener, XMPPStream stream) {
        this.listener = listener;
        this.stream = stream;
        if (stream != null) {
            stream.addBlockListener(this);
        }
        //listener.loginMessage(SR.MS_SASL_STREAM);
    }

    public static String plainAuthText() {
        String plain =
                Strconv.unicodeToUTF(Setup.get().getSettings().getBareJid())
                        + (char) 0x00
                        + Strconv.unicodeToUTF(Setup.get().getSettings().getLogin())
                        + (char) 0x00
                        + Strconv.unicodeToUTF(Setup.get().getSettings().getPassword());
        return Strconv.toBase64(plain);
    }

    public int blockArrived(XMLDataBlock data) {
        //System.out.println(data.toString());
        if (data.getTagName().equals("stream:features")) {
//#if ZLIB
            XMLDataBlock compr = data.getChildBlock("compression");
            if (compr != null && Setup.get().getSettings().useCompression()) {
                if (compr.getChildBlockByText("zlib") != null) {
                    // negotiating compression
                    XMLDataBlock askCompr = new XMLDataBlock("compress", null, null);
                    askCompr.setNameSpace("http://jabber.org/protocol/compress");
                    askCompr.addChild("method", "zlib");
                    stream.send(askCompr);
                    listener.loginMessage(SR.MS_ZLIB);
                    return XMLBlockListener.BLOCK_PROCESSED;
                }
            }
//#endif
            XMLDataBlock mech = data.getChildBlock("mechanisms");
            if (mech != null) {
                // first stream - step 1. selecting authentication mechanism
                //common body
                XMLDataBlock auth = new XMLDataBlock("auth", null, null);
                auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");

                // DIGEST-MD5 mechanism
                if (mech.getChildBlockByText("DIGEST-MD5") != null) {
                    auth.setAttribute("mechanism", "DIGEST-MD5");

                    //System.out.println(auth.toString());

                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH);
                    return XMLBlockListener.BLOCK_PROCESSED;
                }


                if (mech.getChildBlockByText("PLAIN") != null) {

                    if (!Setup.get().getSettings().getPlainAuth()) {
                        listener.loginFailed("SASL: Plain auth required");
                        return XMLBlockListener.NO_MORE_BLOCKS;
                    }

                    auth.setAttribute("mechanism", "PLAIN");

                    auth.setText(plainAuthText());

                    stream.send(auth);
                    listener.loginMessage(SR.MS_AUTH);
                    return XMLBlockListener.BLOCK_PROCESSED;
                }
                // no more method found
                listener.loginFailed("SASL: Unknown mechanisms");
                return XMLBlockListener.NO_MORE_BLOCKS;

            } //SASL mechanisms

            // second stream - step 1. binding resource
            else if (data.getChildBlock("bind") != null) {
                XMLDataBlock bindIq = new Iq(null, Iq.TYPE_SET, "bind");
                XMLDataBlock bind = bindIq.addChildNs("bind", "urn:ietf:params:xml:ns:xmpp-bind");
                bind.addChild("resource", Setup.get().getSettings().getResource());
                stream.send(bindIq);

                listener.loginMessage(SR.MS_RESOURCE_BINDING);

                return XMLBlockListener.BLOCK_PROCESSED;
            }

//#ifdef NON_SASL_AUTH
            if (data.findNamespace("auth", "http://jabber.org/features/iq-auth") != null) {
                new NonSASLAuth(listener, stream);
                return XMLBlockListener.NO_MORE_BLOCKS;
            }
//#endif

            //fallback if no known authentication methods were found
            listener.loginFailed("No known authentication methods");
            return XMLBlockListener.NO_MORE_BLOCKS;
        } else if (data.getTagName().equals("challenge")) {
            // first stream - step 2,3. reaction to challenges

            String challenge = decodeBase64(data.getText());
            //System.out.println(challenge);

            XMLDataBlock resp = new XMLDataBlock("response", null, null);
            resp.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");

            int nonceIndex = challenge.indexOf("nonce=");
            // first stream - step 2. generating DIGEST-MD5 response due to challenge
            if (nonceIndex >= 0) {
                nonceIndex += 7;
                String nonce = challenge.substring(nonceIndex, challenge.indexOf('\"', nonceIndex));
                String cnonce = Setup.get().getSettings().getProperty("SC-Mobile-CNonce", "123456789abcd");

                String login = Setup.get().getSettings().getLogin();
                if (login == null) {
                    login = "";
                }
                String password = Setup.get().getSettings().getPassword();
                if (password == null) {
                    password = "";
                }
                resp.setText(responseMd5Digest(
                        Strconv.unicodeToUTF(login),
                        Strconv.unicodeToUTF(password),
                        Setup.get().getSettings().getServerName(),
                        "xmpp/" + Setup.get().getSettings().getServerName(),
                        nonce,
                        cnonce));
                //System.out.println(resp.toString());
            }
            // first stream - step 3. sending second empty response due to second challenge
            //if (challenge.startsWith("rspauth")) {}

            stream.send(resp);
            return XMLBlockListener.BLOCK_PROCESSED;
        }
//#if ZLIB
        else if (data.getTagName().equals("compressed")) {
            if (!Setup.get().getSettings().getBooleanProperty("session.quickstart", "false")) {
                stream.setZlibCompression();
                try {
                    stream.initiateStream();
                } catch (IOException ex) {
                }
                return XMLBlockListener.NO_MORE_BLOCKS;
            } else {
                return XMLBlockListener.BLOCK_PROCESSED;
            }
        }

//#endif

        else if (data.getTagName().equals("failure")) {
            // first stream - step 4a. not authorized
            listener.loginFailed(XmppError.decodeSaslError(data).toString());
        } else if (data.getTagName().equals("success")) {
            // first stream - step 4b. success.
            if (!Setup.get().getSettings().getBooleanProperty("session.quickstart", "false")) {
                try {
                    stream.initiateStream();
                } catch (IOException ex) {
                }
                return XMLBlockListener.NO_MORE_BLOCKS; // at first stream
            } else {
                return XMLBlockListener.BLOCK_PROCESSED;
            }
        }

        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
                // second stream - step 2. resource binded - opening session
                if (data.getAttribute("id").equals("bind")) {
                    String myJid = data.getChildBlock("bind").getChildBlockText("jid");
                    listener.bindResource(myJid);
                    if (!Setup.get().getSettings().getBooleanProperty("session.quickstart", "false")) {
                        XMLDataBlock session = new Iq(null, Iq.TYPE_SET, "sess");
                        session.addChildNs("session", "urn:ietf:params:xml:ns:xmpp-session");
                        stream.send(session);
                    }
                    listener.loginMessage(SR.MS_SESSION);
                    return XMLBlockListener.BLOCK_PROCESSED;

                    // second stream - step 3. session opened - reporting success login
                } else if (data.getAttribute("id").equals("sess")) {
                    listener.loginSuccess(XMPPClient.getInstance().isTryToLogin());
                    return XMLBlockListener.NO_MORE_BLOCKS;
                    //return XMLBlockListener.BLOCK_PROCESSED;
                }
            }
        }
        return XMLBlockListener.BLOCK_REJECTED;
    }

    private String decodeBase64(String src) {
        int len = 0;
        int ibuf = 1;
        StringBuffer out = new StringBuffer();

        for (int i = 0; i < src.length(); i++) {
            int nextChar = src.charAt(i);
            int base64 = -1;
            if (nextChar > 'A' - 1 && nextChar < 'Z' + 1) {
                base64 = nextChar - 'A';
            } else if (nextChar > 'a' - 1 && nextChar < 'z' + 1) {
                base64 = nextChar + 26 - 'a';
            } else if (nextChar > '0' - 1 && nextChar < '9' + 1) {
                base64 = nextChar + 52 - '0';
            } else if (nextChar == '+') {
                base64 = 62;
            } else if (nextChar == '/') {
                base64 = 63;
            } else if (nextChar == '=') {
                base64 = 0;
                len++;
            } else if (nextChar == '<') {
                break;
            }
            if (base64 >= 0) {
                ibuf = (ibuf << 6) + base64;
            }
            if (ibuf >= 0x01000000) {
                out.append((char) ((ibuf >> 16) & 0xff));
                if (len < 2) {
                    out.append((char) ((ibuf >> 8) & 0xff));
                }
                if (len == 0) {
                    out.append((char) (ibuf & 0xff));
                }
                //len+=3;
                ibuf = 1;
            }
        }
        return out.toString();
    }

    /**
     * This routine generates MD5-DIGEST response via SASL specification
     *
     * @param user
     * @param pass
     * @param realm
     * @param digestUri
     * @param nonce
     * @param cnonce
     * @return
     */
    private String responseMd5Digest(String user, String pass, String realm, String digestUri, String nonce, String cnonce) {

        MD5 hUserRealmPass = new MD5();
        hUserRealmPass.init();
        hUserRealmPass.updateASCII(user);
        hUserRealmPass.update((byte) ':');
        hUserRealmPass.updateASCII(realm);
        hUserRealmPass.update((byte) ':');
        hUserRealmPass.updateASCII(pass);
        hUserRealmPass.finish();

        MD5 hA1 = new MD5();
        hA1.init();
        hA1.update(hUserRealmPass.getDigestBits());
        hA1.update((byte) ':');
        hA1.updateASCII(nonce);
        hA1.update((byte) ':');
        hA1.updateASCII(cnonce);
        hA1.finish();

        MD5 hA2 = new MD5();
        hA2.init();
        hA2.updateASCII("AUTHENTICATE:");
        hA2.updateASCII(digestUri);
        hA2.finish();

        MD5 hResp = new MD5();
        hResp.init();
        hResp.updateASCII(hA1.getDigestHex());
        hResp.update((byte) ':');
        hResp.updateASCII(nonce);
        hResp.updateASCII(":00000001:");
        hResp.updateASCII(cnonce);
        hResp.updateASCII(":auth:");
        hResp.updateASCII(hA2.getDigestHex());
        hResp.finish();

        String out = "username=\"" + user + "\",realm=\"" + realm + "\"," +
                "nonce=\"" + nonce + "\",nc=00000001,cnonce=\"" + cnonce + "\"," +
                "qop=auth,digest-uri=\"" + digestUri + "\"," +
                "response=\"" + hResp.getDigestHex() + "\",charset=utf-8";
        String resp = Strconv.toBase64(out);
        //System.out.println(decodeBase64(resp));

        return resp;
    }

}
