package com.magenta.mc.client.xmpp.extensions.dataforms;

/**
 * Created 02.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class ChoiceGroup implements Item {
    public static final int MULTIPLE = 0;
    public static final int SINGLE = 1;

    public ChoiceGroup(String label, int type) {
    }

    public int append(String label, Object o) {
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setSelectedIndex(int index, boolean set) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public boolean isSelected(int i) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public int size() {
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }

    public int getSelectedIndex() {
        return 0;
    }
}
