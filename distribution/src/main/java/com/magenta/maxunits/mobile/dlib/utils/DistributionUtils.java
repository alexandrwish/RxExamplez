package com.magenta.maxunits.mobile.dlib.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.magenta.maxunits.distribution.R;
import com.magenta.maxunits.mobile.dlib.service.storage.entity.Stop;

public class DistributionUtils {

    public static Drawable getStopPriorityIcon(Context context, Stop stop) {
        Drawable icon = context.getResources().getDrawable(stop.isDrop() ? R.drawable.img_minor : R.drawable.img_normal).mutate();
        int color = Color.WHITE;
        switch (stop.getPriority()) {
            case 2: {
                color = Color.RED;
                break;
            }
            case 1: {
                color = Color.YELLOW;
                break;
            }
            case 0: {
                color = Color.GREEN;
                break;
            }
        }
        icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return icon;
    }

    public static boolean getBoolean(String s) {
        return !(s == null || s.trim().isEmpty()) && Boolean.parseBoolean(s);
    }
}