/*
 * XDataForm.java
 *
 * Created on 6 Май 2008 г., 0:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.magenta.mc.client.xmpp.extensions.dataforms;

import com.magenta.mc.client.locale.SR;
import com.magenta.mc.client.util.Strconv;
import com.magenta.mc.client.xml.XMLDataBlock;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Jabber data forms, можно использовать для настраиваемых опросов.
 * Например, для проверки машины.
 * http://xmpp.org/extensions/xep-0004.html
 *
 * @author root
 */
public class XDataForm {

    Vector items;
    Form f;
    private NotifyListener notifyListener;
    private Command cmdOk = new Command(SR.MS_SEND, Command.OK /*Command.SCREEN*/, 1);
    private Command cmdCancel = new Command(SR.MS_BACK, Command.BACK, 99);


    /**
     * Creates a new instance of XDataForm
     */
    public XDataForm(XMLDataBlock form, NotifyListener notifyListener) {
        this.notifyListener = notifyListener;

        String title = form.getChildBlockText("title");
        f = new Form() {

            public int append(String text) {
                return 0;  //todo
            }

            public int append(Item formItem) {
                return 0;  //todo
            }
        };

        items = new Vector();

        for (Enumeration e = form.getChildBlocks().elements(); e.hasMoreElements(); ) {

            XMLDataBlock ch = (XMLDataBlock) e.nextElement();

            if (ch.getTagName().equals("instructions")) {
                f.append(ch.getText());
                f.append("\n");
                continue;
            }

            if (!ch.getTagName().equals("field")) {
                continue;
            }

            XDataField field = new XDataField(ch);

            items.addElement(field);

            if (field.hidden) {
                continue;
            }

            if (field.media != null) {
                field.mediaIndex = f.append(field.media);
            }
            field.formIndex = f.append(field.formItem);
        }

        // todo:
        /*f.setCommandListener(this);
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);*/
    }

    public void fetchMediaElements(Vector bobCache) {
        //TODO: fetch external http bobs and non-cached in-band bobs
        for (int i = 0; i < items.size(); i++) {
            XDataField field = (XDataField) items.elementAt(i);
            if (field.mediaUri == null) {
                continue;
            }
            if (!(field.media instanceof StringItem)) {
                continue;
            }

            if (field.mediaUri.startsWith("cid:")) {
                String cid = field.mediaUri.substring(4);
                if (bobCache == null) {
                    continue;
                } //TODO: in-band bob request

                for (int bob = 0; bob < bobCache.size(); bob++) {
                    XMLDataBlock data = (XMLDataBlock) bobCache.elementAt(bob);
                    if (data.isJabberNameSpace("urn:xmpp:bob") && cid.equals(data.getAttribute("cid"))) {
                        byte[] bytes = Strconv.fromBase64(data.getText());
                        // todo: image
                        /*Image img=Image.createImage(bytes, 0, bytes.length);
                        f.set(field.mediaIndex, new ImageItem(null, img, Item.LAYOUT_CENTER, null));*/
                    }
                }
            }
        }
    }

    public void commandAction(Command command) {
        if (command == cmdOk) {
            XMLDataBlock resultForm = new XMLDataBlock("x", null, null);
            resultForm.setNameSpace("jabber:x:data");
            resultForm.setTypeAttribute("submit");

            for (Enumeration e = items.elements(); e.hasMoreElements(); ) {
                XMLDataBlock ch = ((XDataField) e.nextElement()).constructJabberDataBlock();
                if (ch != null) {
                    resultForm.addChild(ch);
                }
            }
            notifyListener.XDataFormSubmit(resultForm);
        }
        //display.setCurrent(parentView);
    }

    public interface NotifyListener {
        void XDataFormSubmit(XMLDataBlock form);
    }

    private class Command {
        public static final int OK = 0;
        public static final int BACK = 1;
        String text;
        int type;
        int i;

        public Command(String text, int type, int i) {
        }
    }
}
