package com.magenta.mc.client.log;

import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: popov
 * Date: 21.09.12
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class ServerTime {

    private static ThreadLocal tl = new ThreadLocal();

    public static String get() {
        Settings settings = Setup.get().getSettings();
        return getDF().format(new Date(System.currentTimeMillis() + settings.getTimeDelta() + settings.getServerTimezoneOffset() * 1000 * 60 * 60));
    }

    public static String get(long mobileTs) {
        Settings settings = Setup.get().getSettings();
        return getDF().format(new Date(mobileTs + settings.getTimeDelta() + settings.getServerTimezoneOffset() * 1000 * 60 * 60));
    }

    private static DateFormat getDF() {
        if (tl.get() == null) {
            DateFormat DF = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
            DF.setTimeZone(TimeZone.getTimeZone("UTC"));
            tl.set(DF);
        }
        return (DateFormat) tl.get();
    }

}
