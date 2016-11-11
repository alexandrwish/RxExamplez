package com.magenta.mc.client.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Petr Popov
 *         Created: 23.01.12 16:47
 */
public class DateFormatter {

    private static final DateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");

    static {
        UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        UTC_DATE_FORMAT.setLenient(false);
    }

    public static synchronized Date parseFromUTC(String utc) throws ParseException {
        return UTC_DATE_FORMAT.parse(utc);
    }
}
