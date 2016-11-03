package com.magenta.mc.client.client;

/**
 * Created 01.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class ExtendedStatus {
    int index;
    private String name;    // status name
    private String status = "";
    private int priority;
    private String screenName;

    /**
     * Creates a new instance of ExtendedStatus
     */
    public ExtendedStatus(int index, String name, String showName) {
        this.index = index;
        this.name = name;
        this.screenName = showName;
    }

    //public void onSelect(){}
    public String toString() {
        StringBuffer s = new StringBuffer(screenName);
        s.append(" (")
                .append(priority)
                .append(") ");
        if (status.length() > 0) {
            s.append('"')
                    .append(status)
                    .append('"');
        }

        //return name+" ("+priority+") \""+status+"\"";
        return s.toString();
    }

    public int getColor() {
        return 0;
    }//return Colors.LIST_INK;}

    public int getImageIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return status;
    }

    public void setMessage(String s) {
        status = s;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int p) {
        priority = p;
    }

    public String getScreenName() {
        return screenName;
    }
}
