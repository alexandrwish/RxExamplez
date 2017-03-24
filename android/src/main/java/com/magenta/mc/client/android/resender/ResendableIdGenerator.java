package com.magenta.mc.client.android.resender;

public class ResendableIdGenerator {

    private static final Object idGenerationLock = new Object();
    private static long previousId;

    public static long generateId() {
        long newId = System.currentTimeMillis();
        synchronized (idGenerationLock) {
            if (newId > previousId) {
                previousId = newId;
            } else {
                newId = ++previousId;
            }
        }
        return newId;
    }
}