package net.jandie1505.playerlevels.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProcessingMap<K, V> implements Map<K, V> {
    @NotNull private final Map<K, V> delegate;
    @NotNull private final Processor<K, V> processor;

    public ProcessingMap(@NotNull Map<K, V> delegate, @NotNull Processor<K, V> processor) {
        this.delegate = delegate;
        this.processor = processor;
    }

    // ----- MAP -----

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public @Nullable V put(K key, V value) {
        V processed = this.processor.process(key, value);
        this.delegate.put(key, processed);
        return processed;
    }

    @Override
    public V remove(Object key) {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            V processed = this.processor.process(entry.getKey(), entry.getValue());
            this.delegate.put(entry.getKey(), processed);
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return Set.of();
    }

    @Override
    public @NotNull Collection<V> values() {
        return List.of();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return Set.of();
    }

    // ----- DELEGATE -----


    public final @NotNull Map<K, V> getDelegate() {
        return this.delegate;
    }

    public final @NotNull Processor<K, V> getProcessor() {
        return this.processor;
    }

    // ----- OTHER -----

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    // ----- INTERFACE -----

    public interface Processor<K, V> {
        V process(K key, V value);
    }

}
