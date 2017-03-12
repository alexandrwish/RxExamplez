package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.ui.theme.Theme;

import static com.magenta.mc.client.android.ui.theme.Theme.DAY;
import static com.magenta.mc.client.android.ui.theme.Theme.NIGHT;

public class ThemeUtils {

    public static void switchTheme() {
        Theme theme;
        switch (McAndroidApplication.getInstance().getThemeManager().getCurrentTheme()) {
            case DAY: {
                theme = NIGHT;
                break;
            }
            case NIGHT: {
                theme = DAY;
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
        switchToTheme(theme);
    }

    public static void switchToTheme(Theme theme) {
        McAndroidApplication.getInstance().getThemeManager().switchToTheme(theme);
        Settings.SettingsBuilder.get().start().setTheme(String.valueOf(theme.getCode())).apply();
    }

    public static void switchToTheme(Integer code) {
        switchToTheme(lookup(code));
    }

    public static Theme lookup(Integer code) {
        if (code == null) {
            return NIGHT;
        }
        switch (code) {
            case 0:
                return NIGHT;
            case 1:
                return DAY;
            default:
                throw new IllegalArgumentException();
        }
    }
}