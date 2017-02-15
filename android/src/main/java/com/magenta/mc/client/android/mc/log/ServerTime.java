package com.magenta.mc.client.android.mc.log;

import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.setup.Setup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ServerTime {

    private static ThreadLocal<DateFormat> tl = new ThreadLocal<>();

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
            DateFormat DF = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.UK);
            DF.setTimeZone(TimeZone.getTimeZone("UTC"));
            tl.set(DF);
        }
        return tl.get();
    }
}