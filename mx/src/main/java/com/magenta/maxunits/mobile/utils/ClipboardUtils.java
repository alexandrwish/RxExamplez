package com.magenta.maxunits.mobile.utils;

import android.content.Context;
import android.text.ClipboardManager;

/**
 * @author Sergey Grachev
 */
public final class ClipboardUtils {
    private ClipboardUtils() {
    }

    public static void copy(final Context ctx, final String text) {
        final ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
    }
}
