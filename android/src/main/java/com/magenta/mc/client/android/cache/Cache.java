package com.magenta.mc.client.android.cache;

public interface Cache<K, V, P> {

    V lookup(K key);

    V lookup(K key, P param);

    interface Callback<K, V, P> {

        V onFetch(K key, P param);
    }
}