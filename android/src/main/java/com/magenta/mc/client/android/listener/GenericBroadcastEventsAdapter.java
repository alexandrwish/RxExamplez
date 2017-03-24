package com.magenta.mc.client.android.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class GenericBroadcastEventsAdapter<E extends BroadcastEvent<T>, T> implements BroadcastEventsListener<E, T> {

    protected final Set<T> filter;
    protected final String id;

    public GenericBroadcastEventsAdapter(final T... filter) {
        this.filter = new HashSet<>(Arrays.asList(filter));
        this.id = this.getClass().getName();
    }

    public GenericBroadcastEventsAdapter(final Object owner, final T... filter) {
        this(owner.getClass(), filter);
    }

    public GenericBroadcastEventsAdapter(final Class owner, final T... filter) {
        this.filter = new HashSet<T>(Arrays.asList(filter));
        this.id = (owner == null ? this.getClass().getName() : owner.getName());
    }

    public GenericBroadcastEventsAdapter(final String id, final T... filter) {
        if (id == null) {
            throw new NullPointerException("Id is null");
        }
        this.filter = new HashSet<>(Arrays.asList(filter));
        this.id = id;
    }

    public Set<T> getFilter() {
        return filter;
    }

    public String getId() {
        return id;
    }
}