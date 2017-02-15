package com.magenta.mc.client.android.mc.login;

import com.magenta.mc.client.android.mc.client.XMPPClient;
import com.magenta.mc.client.android.mc.crypto.MD5;
import com.magenta.mc.client.android.mc.locale.SR;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.mc.util.Strconv;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.XMPPStream;
import com.magenta.mc.client.android.rpc.xmpp.XmppError;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;

import java.io.IOException;

public class SASLAuth implements XMLBlockListener {

    private LoginListener listener;
    private XMPPStream stream;

    public SASLAuth(LoginListener listener, XMPPStream stream) {
        this.listener = listener;
        this.stream = stream;
        if (stream != null) {
            stream.addBlockListener(this);
        }
    }

    public static String plainAuthText() {
        String plain = Strconv.unicodeToUTF(Setup.get().getSettings().getBareJid())
                + (char) 0x00
                + Strconv.unicodeToUTF(Setup.get().getSettings().getLogin())
                + (char) 0x00
                + Strconv.unicodeToUTF(Setup.get().getSettings().getPassword());
        return Strconv.toBase64(plain);
    }

    public int blockArrived(XMLDataBlock data) {
        switch (data.getTagName()) {
            case "stream:features":
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
                XMLDataBlock mech = data.getChildBlock("mechanisms");
                if (mech != null) {
                    XMLDataBlock auth = new XMLDataBlock("auth", null, null);
                    auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
                    if (mech.getChildBlockByText("DIGEST-MD5") != null) {
                        auth.setAttribute("mechanism", "DIGEST-MD5");
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
                    listener.loginFailed("SASL: Unknown mechanisms");
                    return XMLBlockListener.NO_MORE_BLOCKS;
                } else if (data.getChildBlock("bind") != null) {
                    XMLDataBlock bindIq = new Iq(null, Iq.TYPE_SET, "bind");
                    XMLDataBlock bind = bindIq.addChildNs("bind", "urn:ietf:params:xml:ns:xmpp-bind");
                    bind.addChild("resource", Setup.get().getSettings().getResource());
                    stream.send(bindIq);
                    listener.loginMessage(SR.MS_RESOURCE_BINDING);
                    return XMLBlockListener.BLOCK_PROCESSED;
                }
                if (data.findNamespace("auth", "http://jabber.org/features/iq-auth") != null) {
                    new NonSASLAuth(listener, stream);
                    return XMLBlockListener.NO_MORE_BLOCKS;
                }
                listener.loginFailed("No known authentication methods");
                return XMLBlockListener.NO_MORE_BLOCKS;
            case "challenge":
                String challenge = decodeBase64(data.getText());
                XMLDataBlock resp = new XMLDataBlock("response", null, null);
                resp.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
                int nonceIndex = challenge.indexOf("nonce=");
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
                }
                stream.send(resp);
                return XMLBlockListener.BLOCK_PROCESSED;
            case "compressed":
                if (!Setup.get().getSettings().getBooleanProperty("session.quickstart", "false")) {
                    stream.setZlibCompression();
                    try {
                        stream.initiateStream();
                    } catch (IOException ignore) {
                    }
                    return XMLBlockListener.NO_MORE_BLOCKS;
                } else {
                    return XMLBlockListener.BLOCK_PROCESSED;
                }
            case "failure":
                listener.loginFailed(XmppError.decodeSaslError(data).toString());
                break;
            case "success":
                if (!Setup.get().getSettings().getBooleanProperty("session.quickstart", "false")) {
                    try {
                        stream.initiateStream();
                    } catch (IOException ignore) {
                    }
                    return XMLBlockListener.NO_MORE_BLOCKS; // at first stream
                } else {
                    return XMLBlockListener.BLOCK_PROCESSED;
                }
        }
        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
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
                } else if (data.getAttribute("id").equals("sess")) {
                    listener.loginSuccess(XMPPClient.getInstance().isTryToLogin());
                    return XMLBlockListener.NO_MORE_BLOCKS;
                }
            }
        }
        return XMLBlockListener.BLOCK_REJECTED;
    }

    private String decodeBase64(String src) {
        int len = 0;
        int ibuf = 1;
        StringBuilder out = new StringBuilder();
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
                ibuf = 1;
            }
        }
        return out.toString();
    }

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
        return Strconv.toBase64(out);
    }
}