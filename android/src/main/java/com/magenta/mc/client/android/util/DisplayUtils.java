package com.magenta.mc.client.android.util;

import android.content.Context;
import android.util.DisplayMetrics;

public final class DisplayUtils {

    private DisplayUtils() {
    }

    public static int px2dp(final Context context, final int px) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / (metrics.densityDpi / DisplayMetrics.DENSITY_MEDIUM);
    }

    public static int dp2px(final Context context, final int dp) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_MEDIUM);
    }

    public static float density(final Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}