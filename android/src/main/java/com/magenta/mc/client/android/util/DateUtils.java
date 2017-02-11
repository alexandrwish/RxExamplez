package com.magenta.mc.client.android.util;

import android.content.Context;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.util.Resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.UK);

    public static Date parseDate(String dateString, Date defaultDate) {
        Date date = defaultDate;
        if (dateString != null && dateString.trim().length() > 0) {
            try {
                date = Resources.UTC_DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static String toStringTime(final Date date) {
        return TIME_FORMAT.format(date);
    }

    public static String toStringTime(final long value) {
        final long hours = value >= 60 ? value / 60 / 60 : 0;
        final long minutes = hours > 0 ? value % (hours * 60 * 60) / 60 : value / 60;
        return String.format(Locale.UK, "%02d:%02d", hours, minutes);
    }

    public static String toString(final Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String timeToHumanReadable(int seconds, Context context) {
        int tmp = seconds / 60;
        int minutes = tmp % 60;
        tmp = tmp / 60;
        int hours = tmp % 24;
        tmp = tmp / 24;
        int days = tmp;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(context != null ? context.getString(R.string.mx_time_short_days) : 'd');
        }
        if (hours > 0) {
            sb.append(sb.length() > 0 ? ' ' : "").append(hours).append(context != null ? context.getString(R.string.mx_time_short_hours) : 'h');
        }
        if (minutes > 0) {
            sb.append(sb.length() > 0 ? ' ' : "").append(minutes).append(context != null ? context.getString(R.string.mx_time_short_minutes) : 'm');
        }
        return sb.toString();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDateFromCurrent(int amountOfDays) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, amountOfDays);
        return yesterday.getTime();
    }
}