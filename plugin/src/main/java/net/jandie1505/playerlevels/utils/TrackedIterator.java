package net.jandie1505.playerlevels.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

public class TrackedIterator<T> implements Iterator<T> {
    @NotNull private final Iterator<T> delegate;
    @NotNull private final Callback<T> callback;
    private T next;

    public TrackedIterator(@NotNull Iterator<T> delegate, @NotNull Callback<T> callback) {
        this.delegate = delegate;
        this.callback = callback;
        this.next = null;
    }

    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    @Override
    public T next() {
        T next = this.delegate.next();
        this.next = next;
        this.callback.onUpdate(this, Action.NEXT, next);
        return next;
    }

    @Override
    public void remove() {
        this.delegate.remove();
        this.callback.onUpdate(this, Action.REMOVE, this.next);
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        this.delegate.forEachRemaining(action);
    }

    public T getNext() {
        return this.next;
    }

    public interface Callback<T> {
        void onUpdate(@NotNull TrackedIterator<T> trackedIterator, @NotNull Action action, T element);
    }

    public enum Action {
        NEXT,
        REMOVE;
    }

}
