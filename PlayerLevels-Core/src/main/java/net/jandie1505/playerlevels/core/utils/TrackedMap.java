package net.jandie1505.playerlevels.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TrackedMap<K,V> implements Map<K,V> {
    @NotNull private final Map<K,V> delegate;
    @NotNull private final Callback<K, V> callback;

    public TrackedMap(@NotNull Map<K,V> delegate, @NotNull Callback<K, V> callback) {
        this.delegate = delegate;
        this.callback = callback;
    }

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
        V result = this.delegate.put(key, value);
        this.callback.onUpdate(this, Action.PUT, key, value, result);
        return result;
    }

    @Override
    public V remove(Object key) {
        V result = this.delegate.remove(key);
        this.callback.onUpdate(this, Action.REMOVE, key, result, null);
        return result;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
        this.callback.onUpdate(this, Action.CLEAR, null, null, null);
    }

    @Override
    public @NotNull Set<K> keySet() {
        return new TrackedSet<>(this.delegate.keySet(), (set, action, t, result) -> {
            this.callback.onUpdate(this, Action.fromTrackedSet(action), t, null, result);
        });
    }

    @Override
    public @NotNull Collection<V> values() {
        return Collections.unmodifiableCollection(this.delegate.values());
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return new TrackedSet<>(this.delegate.entrySet(), (set, action, t, result) -> {
            this.callback.onUpdate(this, Action.fromTrackedSet(action), t, null, result);
        });
    }

    // ----- DELEGATE -----

    public final @NotNull Map<K, V> getDelegate() {
        return delegate;
    }

    // ----- HASH -----

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    // ----- CALLBACK -----

    public interface Callback<K, V> {
        void onUpdate(@NotNull TrackedMap<K, V> map, @NotNull Action action, Object key, Object value, Object result);
    }

    public enum Action {
        PUT(true, false),
        REMOVE(false, true),
        CLEAR(false, true),
        KEYSET_ADD(true, false),
        KEYSET_REMOVE(false, true);

        private boolean isAdd;
        private boolean isRemove;

        Action(boolean isAdd, boolean isRemove) {
            this.isAdd = isAdd;
            this.isRemove = isRemove;
        }

        public boolean isAdd() {
            return isAdd;
        }

        public boolean isRemove() {
            return isRemove;
        }

        public static Action fromTrackedSet(@NotNull TrackedSet.Action action) {
            return switch (action) {
                case ADD -> Action.KEYSET_ADD;
                case REMOVE -> Action.KEYSET_REMOVE;
                case CLEAR -> Action.CLEAR;
            };
        }

    }

}
