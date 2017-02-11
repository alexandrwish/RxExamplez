package com.magenta.mc.client.android.service.listeners;

public class BroadcastEvent<T> {

    private final T type;

    public BroadcastEvent(final T type) {
        this.type = type;
    }

    public T getType() {
        return type;
    }

    public boolean isAny(final T... types) {
        for (final T type : types) {
            if (this.type.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean is(final T type) {
        return this.type.equals(type);
    }
}