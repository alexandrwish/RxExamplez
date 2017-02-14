package com.magenta.mc.client.android.mc.client.resend;

/**
 * @author Petr Popov
 *         Created: 23.01.12 15:23
 */
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
