package com.magenta.maxunits.mobile.utils;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergey Grachev
 */
public final class StringUtils {

    private static final NumberFormat DOUBLE_FORMAT = new DecimalFormat("#.#####");
    private static final NumberFormat COST_FORMAT = new DecimalFormat("#.##");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DAY_AND_MONTH_FORMAT = new SimpleDateFormat("dd/MM");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private StringUtils() {
    }

    public static boolean isBlank(final String s) {
        return s == null || "".equals(s.trim());
    }

    public static String toBlank(final String s) {
        return isBlank(s) ? null : s.trim();
    }

    public static String unscreening(String string) {
        return StringUtils.isBlank(string) ? "" : string.replace("'", "\\").replace("\u00a1", "\"");
    }

    public static String getTimeRange(Long start, Long end) {
        return String.format("%s - %s", DateUtils.toStringTime(new Date(TimeUnit.SECONDS.toMillis(start))), DateUtils.toStringTime(new Date(TimeUnit.SECONDS.toMillis(end))));
    }

    public static String formatDouble(Double value) {
        return DOUBLE_FORMAT.format(value != null ? value : 0.);
    }

    public static String formatCost(Double cost) {
        return COST_FORMAT.format(cost != null ? cost : cost);
    }


    public static String formatDouble(String value) {
        return isBlank(value) ? null : formatDouble(Double.valueOf(value));
    }

    public static String formatCost(String cost) {
        return isBlank(cost) ? null : formatCost(Double.valueOf(cost));
    }

    public static String formatDateTime(String value) {
        return DATE_TIME_FORMAT.format(new Date(value));
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    public static String formatDayAndMonth(Date date) {
        return DAY_AND_MONTH_FORMAT.format(date);
    }

    public static String encodeURI(String string) {
        if (isBlank(string)) return string;
        try {
            string = URLEncoder.encode(string, "utf8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            string = Uri.encode(string);
        }
        return string;
    }

    public static String decodeURI(String string) {
        return string != null ? Uri.decode(string) : null;
    }
}