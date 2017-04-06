package com.magenta.mc.client.android.util;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.magenta.mc.client.android.R;

import java.util.Locale;

public final class LocaleUtils {

    private LocaleUtils() {
    }

    public static void changeLocale(final Application application, final String code) {
        final Resources resources = application.getBaseContext().getResources();
        final Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(code == null ? "en" : code));
    }

    public static String getDisplayName(final String code) {
        return new Locale(code).getDisplayName();
    }

    public static String[] listOfAvailableLocales(final Application application) {
        final String[] codes = application.getResources().getStringArray(R.array.mx_locale_list);
        final String[] result = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            result[i] = getDisplayName(codes[i]);
        }
        return result;
    }
}