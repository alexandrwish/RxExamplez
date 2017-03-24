package com.magenta.mc.client.android.util;

import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User: stukov
 * Date: 26.01.2010
 * Time: 14:51:11
 */
public class Resources {
    /*
    главиатура rgb
    65 64 66    hex #414042

    фон
    188 190 192   hex #bcbec0
*/

    //todo SimpleDateFormat is not thread safe !!!!!!!!!
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("dd/MM/yy");
    public static final char DECIMAL_SEPARATOR = new DecimalFormatSymbols(Locale.UK).getDecimalSeparator();
    public static final DateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");

    static {
        UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        UTC_DATE_FORMAT.setLenient(false);
        DATE_FORMAT.setLenient(false);
        DATE_FORMAT_SHORT.setLenient(false);
    }
}
