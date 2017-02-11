package com.magenta.mc.client.android.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CacheLRUMaxAge<K, V, P> implements Cache<K, V, P> {

    private final Callback<K, V, P> callback;
    private final boolean allowNulls;
    private final int maxAge;
    private final Map<K, Entry<V>> cache;

    public CacheLRUMaxAge(final Callback<K, V, P> callback, final int size, final int maxAge) {
        this(callback, size, maxAge, false);
    }

    public CacheLRUMaxAge(final Callback<K, V, P> callback, final int size, final int maxAge, final boolean allowNulls) {
        if (callback == null) {
            throw new NullPointerException("Callback is NULL");
        }
        this.maxAge = maxAge;
        this.allowNulls = allowNulls;
        this.callback = callback;
        //noinspection serial
        this.cache = Collections.synchronizedMap(new LinkedHashMap<K, Entry<V>>(size + 1, 0.75f, true) {
            public boolean removeEldestEntry(final Entry eldest) {
                return size() > size;
            }
        });
    }

    public V lookup(final K key) {
        return lookup(key, null);
    }

    public V lookup(final K key, final P param) {
        if (key == null) {
            throw new NullPointerException("Key is NULL");
        }
        Entry<V> e;
        synchronized (cache) {
            e = cache.get(key);
            if (e == null) {
                final V v = callback.onFetch(key, param);
                if (v != null || allowNulls) {
                    cache.put(key, e = new Entry<V>(System.currentTimeMillis(), v));
                }
            } else {
                e.touch();
            }
            update();
        }

        return e != null ? e.value : null;
    }

    private void update() {
        final Iterator<Map.Entry<K, Entry<V>>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<K, Entry<V>> e = it.next();
            if (System.currentTimeMillis() - e.getValue().timestamp >= maxAge) {
                it.remove();
            }
        }
    }

    private static final class Entry<V> {

        public final V value;
        public long timestamp;

        private Entry(final long timestamp, final V value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public void touch() {
            this.timestamp = System.currentTimeMillis();
        }
    }
}