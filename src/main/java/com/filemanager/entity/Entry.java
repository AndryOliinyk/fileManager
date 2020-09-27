package com.filemanager.entity;

import java.util.Map;

/**
 *
 * Custom class from map only for single value
 * @param <K> key
 * @param <V> value
 *
 * @author Andrii Oliinuk
 */
public final class Entry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}
