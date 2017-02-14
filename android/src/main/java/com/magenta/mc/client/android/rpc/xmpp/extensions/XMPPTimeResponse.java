package com.magenta.mc.client.android.rpc.xmpp.extensions;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.util.Resources;
import com.magenta.mc.client.android.mc.util.StrUtil;
import com.magenta.mc.client.android.mc.xml.XMLBlockListener;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;
import com.magenta.mc.client.android.rpc.xmpp.datablocks.Iq;

import java.text.ParseException;

public class XMPPTimeResponse implements XMLBlockListener {

    private static TimeListener listener;

    public XMPPTimeResponse() {
    }

    public static void setListener(TimeListener listener) {
        XMPPTimeResponse.listener = listener;
    }

    public int blockArrived(XMLDataBlock data) {
        if (!(data instanceof Iq)) {
            return BLOCK_REJECTED;
        }
        if (!data.getAttribute("type").equals("result")) {
            return BLOCK_REJECTED;
        }

        XMLDataBlock timeResponse = data.findNamespace("time", "urn:xmpp:time");
        if (timeResponse == null) {
            return BLOCK_REJECTED;
        }

        final String utcValue = timeResponse.getChildBlockText("utc");

        final String tzoValue = timeResponse.getChildBlockText("tzo");
        int tzoHours = getTZOHours(tzoValue);

        long serverTime = System.currentTimeMillis();
        try {
            serverTime = Resources.UTC_DATE_FORMAT.parse(utcValue).getTime();
        } catch (ParseException e) {
            MCLoggerFactory.getLogger(getClass()).warn("cannot parse incoming UTC time: " + utcValue);
            tzoHours = 0;
        }

        if (XMPPTimeResponse.listener != null) {
            XMPPTimeResponse.listener.gotTime(serverTime, tzoHours);
        }

        return XMLBlockListener.BLOCK_PROCESSED;

    }

    private int getTZOHours(String tzoValue) {
        String tzoHourStr = StrUtil.split(tzoValue, ":")[0];
        int sign = 1;
        if (tzoHourStr.startsWith("-")) {
            sign = -1;
            tzoHourStr = tzoHourStr.substring(1);
        } else if (tzoHourStr.startsWith("+")) {
            tzoHourStr = tzoHourStr.substring(1);
        }
        return Integer.parseInt(tzoHourStr) * sign;
    }

    public interface TimeListener {
        void gotTime(long serverTime, int tzoHours);
    }
}
