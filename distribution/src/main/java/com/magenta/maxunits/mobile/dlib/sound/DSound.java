package com.magenta.maxunits.mobile.dlib.sound;

public enum DSound {
    // ADD new sounds for alert
    SOUND_TADA(0),
    SOUND_SUCCESS(1),
    SOUND_ERROR(2);

    private int num;

    DSound(int num) {
        this.num = num;
    }

    public static String getNameById(int id) {
        switch (id) {
            case (0): {
                return SOUND_TADA.name();
            }
            case (1): {
                return SOUND_SUCCESS.name();
            }
            case (2): {
                return SOUND_ERROR.name();
            }
            default: {
                return "Unknown sound";
            }
        }
    }

    public int getNum() {
        return num;
    }
}