package com.magenta.mc.client.android.ui.theme;

public enum Theme {
    night(0), day(1);

    private final int code;

    Theme(int code) {
        this.code = code;
    }

    public static Theme lookup(int code) {
        switch (code) {
            case 0:
                return night;
            case 1:
                return day;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Theme changeTheme(Theme oldTheme) {
        switch (oldTheme) {
            case day:
                return night;
            case night:
                return day;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int getCode() {
        return code;
    }
}
