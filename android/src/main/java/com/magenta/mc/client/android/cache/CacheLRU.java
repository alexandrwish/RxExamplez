package com.magenta.mc.client.android.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CacheLRU<K, V, P> implements Cache<K, V, P> {

    private final Callback<K, V, P> callback;
    private final boolean allowNulls;
    private final Map<K, V> cache;

    public CacheLRU(final Callback<K, V, P> callback, final int size) {
        this(callback, size, false);
    }

    public CacheLRU(final Callback<K, V, P> callback, final int size, final boolean allowNulls) {
        if (callback == null) {
            throw new NullPointerException("Callback is NULL");
        }
        this.allowNulls = allowNulls;
        this.callback = callback;
        this.cache = Collections.synchronizedMap(new LinkedHashMap<K, V>(size + 1, 0.75f, true) {
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
        V e;
        synchronized (cache) {
            e = cache.get(key);
            if (e == null && (!allowNulls || !cache.containsKey(key))) {
                e = callback.onFetch(key, param);
                if (e != null || allowNulls) {
                    cache.put(key, e);
                }
            }
        }
        return e;
    }
}