package com.magenta.mc.client.android.ui.theme;

public enum Theme {

    NIGHT(0), DAY(1);

    private final int code;

    Theme(int code) {
        this.code = code;
    }

    public static Theme lookup(int code) {
        switch (code) {
            case 0:
                return NIGHT;
            case 1:
                return DAY;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Theme changeTheme(Theme oldTheme) {
        switch (oldTheme) {
            case DAY:
                return NIGHT;
            case NIGHT:
                return DAY;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int getCode() {
        return code;
    }
}