package com.magenta.mc.client.android.rpc.xmpp.dataforms;

import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.util.Enumeration;
import java.util.Vector;

public class XDataField {

    boolean hidden;
    Item formItem;
    int formIndex = -1;
    int mediaIndex = -1;
    String mediaUri;
    Item media;

    private String name;
    private String type;
    private String value;
    private Vector<String> optionsList;

    /**
     * Creates a new instance of XDataField
     */
    public XDataField(XMLDataBlock field) {
        type = field.getAttribute("type");
        name = field.getAttribute("var");
        String label = field.getAttribute("label");
        if (label == null) {
            label = name;
        }
        value = field.getChildBlockText("value");
        boolean required = field.getChildBlock("required") != null;
        if (required) {
            label = label + " *";
        }
        if (type == null) {
            media = extractMedia(field);
            formItem = new TextField(label, value, 200, TextField.ANY);
            return;
        }
        hidden = type.equals("hidden");
        switch (type) {
            case "fixed":
                formItem = new StringItem(label, value);
                break;
            case "boolean": {
                ChoiceGroup ch = new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
                formItem = ch;
                ch.append(label, null);
                boolean set = false;
                if (value.equals("1")) {
                    set = true;
                }
                if (value.equals("true")) {
                    set = true;
                }
                ch.setSelectedIndex(0, set);
                break;
            }
            case "list-single":
            case "list-multi": {
                int choiceType = (type.equals("list-single")) ? ChoiceGroup.SINGLE : ChoiceGroup.MULTIPLE;
                ChoiceGroup ch = new ChoiceGroup(label, choiceType);
                formItem = ch;
                optionsList = new Vector<>();
                for (Enumeration e = field.getChildBlocks().elements(); e.hasMoreElements(); ) {
                    XMLDataBlock option = (XMLDataBlock) e.nextElement();
                    if (option.getTagName().equals("option")) {
                        String opValue = option.getChildBlockText("value");
                        String opLabel = option.getAttribute("label");
                        if (opLabel == null) {
                            opLabel = opValue;
                        }
                        optionsList.addElement(opValue);
                        int index = ch.append(opLabel, null);
                        if (value.equals(opValue)) {
                            ch.setSelectedIndex(index, true);
                        }
                    }
                }
                break;
            }
            // text-single, text-private
            default:
                if (value.length() >= 200) {
                    value = value.substring(0, 198);
                }
                int constrains = (type.equals("text-private")) ? TextField.PASSWORD : TextField.ANY;
                formItem = new TextField(label, value, 200, constrains);
                break;
        }
    }

    private Item extractMedia(XMLDataBlock field) {
        try {
            XMLDataBlock m = field.findNamespace("media", "urn:xmpp:media-element");
            if (m == null) {
                return null;
            }
            for (Enumeration e = m.getChildBlocks().elements(); e.hasMoreElements(); ) {
                XMLDataBlock u = (XMLDataBlock) e.nextElement();
                if (u.getTagName().equals("uri")) {
                    if (!u.getTypeAttribute().startsWith("image")) {
                        continue;
                    }
                    mediaUri = u.getText();
                    return new StringItem(null, "[Loading Image]");
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    XMLDataBlock constructJabberDataBlock() {
        XMLDataBlock j = new XMLDataBlock("field", null, null);
        j.setAttribute("var", name);
        if (type != null) {
            j.setAttribute("type", type);
        }
        if (formItem instanceof TextField) {
            String value = ((TextField) formItem).getString();
            j.addChild("value", value);
        }
        if (formItem instanceof ChoiceGroup) {
            //only x:data
            if (type != null) {
                switch (type) {
                    case "boolean":
                        boolean set = ((ChoiceGroup) formItem).isSelected(0);
                        String result = String.valueOf(set);
                        if (value.length() == 1) {
                            result = set ? "1" : "0";
                        }
                        j.addChild("value", result);
                        break;
                    case "list-multi":
                        ChoiceGroup ch = (ChoiceGroup) formItem;
                        int count = ch.size();
                        for (int i = 0; i < count; i++) {
                            if (ch.isSelected(i)) {
                                j.addChild("value", optionsList.elementAt(i));
                            }
                        }
                        break;
                    default:
                        int index = ((ChoiceGroup) formItem).getSelectedIndex();
                        if (index >= 0) {
                            j.addChild("value", optionsList.elementAt(index));
                        }
                        break;
                }
            }
        }
        return j;
    }
}