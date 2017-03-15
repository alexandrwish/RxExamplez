package com.magenta.mc.client.android.mc.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

@Deprecated
// TODO: 3/12/17 fix me
public class ServerTime {

    private static ThreadLocal<DateFormat> tl = new ThreadLocal<>();

    public static String get() {
//        Settings settings = Settings.get();
//        return getDF().format(new Date(System.currentTimeMillis() + settings.getTimeDelta() + settings.getServerTimezoneOffset() * 1000 * 60 * 60));
        return "";
    }

    public static String get(long mobileTs) {
//        Settings settings = Settings.get();
//        return getDF().format(new Date(mobileTs + settings.getTimeDelta() + settings.getServerTimezoneOffset() * 1000 * 60 * 60));
        return "";
    }

    private static DateFormat getDF() {
        if (tl.get() == null) {
            DateFormat DF = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.UK);
            DF.setTimeZone(TimeZone.getTimeZone("UTC"));
            tl.set(DF);
        }
        return tl.get();
    }
}