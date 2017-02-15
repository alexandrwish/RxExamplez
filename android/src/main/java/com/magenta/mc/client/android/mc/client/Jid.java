package com.magenta.mc.client.android.mc.client;

import com.magenta.mc.client.android.mc.util.Strconv;

public class Jid {

    private String bareJid;
    private String resource;

    public Jid(String s) {
        setJid(s);
    }

    public boolean equals(Jid j, boolean compareResource) {
        return j != null && bareJid.equals(j.bareJid) && (!compareResource || (resource.equals(j.resource)));
    }

    public String getServer() {
        try {
            int beginIndex = bareJid.indexOf('@') + 1;
            return bareJid.substring(beginIndex);
        } catch (Exception e) {
            return "-";
        }
    }

    public String getResource() {
        return resource;
    }

    public String getJid() {
        if (resource.length() == 0) return bareJid;
        return bareJid /** +'/' **/ + resource;
    }

    private void setJid(String s) {
        int resourcePos = s.indexOf('/');
        if (resourcePos < 0) resourcePos = s.length();
        resource = s.substring(resourcePos);
        bareJid = Strconv.toLowerCase(s.substring(0, resourcePos));
    }
}