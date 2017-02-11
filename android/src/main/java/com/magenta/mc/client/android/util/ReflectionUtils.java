package com.magenta.mc.client.android.util;

import java.lang.reflect.Field;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static <T> T getValueOfPrivateField(final Class clazz, final Object obj, final String name) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            //noinspection unchecked
            return (T) field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
        return null;
    }

    public static void setValueOfPrivateField(final Class clazz, final Object obj, final String name, final Object value) {
        final Field field;
        try {
            field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}