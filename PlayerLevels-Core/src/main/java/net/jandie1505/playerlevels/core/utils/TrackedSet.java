package net.jandie1505.playerlevels.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TrackedSet<T> implements Set<T> {
    @NotNull private final Set<T> delegate;
    @NotNull private final Callback<T> callback;

    public TrackedSet(@NotNull Set<T> delegate, @NotNull Callback<T> callback) {
        this.delegate = delegate;
        this.callback = callback;
    }

    // ----- IMPLEMENTATION -----

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new TrackedIterator<>(this.delegate.iterator(), (trackedIterator, action, element) -> {
            if (action == TrackedIterator.Action.REMOVE) this.callback.onUpdate(this, Action.REMOVE, element, true);
        });
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public @NotNull <T1> T1 @NotNull [] toArray(@NotNull T1[] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean result = this.delegate.add(t);
        this.callback.onUpdate(this, Action.ADD, t, result);
        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = this.delegate.remove(o);
        this.callback.onUpdate(this, Action.REMOVE, o, result);
        return result;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        boolean changed = false;

        for (T t : c) {
            boolean result = this.add(t);
            if (result) changed = true;
        }

        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean changed = false;
        Iterator<T> iterator = this.iterator();

        while (iterator.hasNext()) {
            T element = iterator.next();
            if (!c.contains(element)) {
                iterator.remove();
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;

        for (Object t : c) {
            boolean result = this.remove(t);
            if (result) changed = true;
        }

        return changed;
    }

    @Override
    public void clear() {
        this.delegate.clear();
        this.callback.onUpdate(this, Action.CLEAR, null, true);
    }

    // ----- OTHER -----

    public Set<T> getDelegate() {
        return delegate;
    }

    // ----- HASH -----

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    // ----- INNER CLASSES -----

    public interface Callback<T> {
        void onUpdate(@NotNull TrackedSet<T> set, @NotNull Action action, Object t, boolean result);
    }

    public enum Action {
        ADD,
        REMOVE,
        CLEAR;
    }

}
