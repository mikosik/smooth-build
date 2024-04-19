package org.smoothbuild.common.concurrent;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;

/**
 * This class is thread-safe.
 * Consumers registered with {@link #addConsumer(Consumer)} are called without any lock held.
 */
public class PromisedValue<T> implements Consumer<T>, Promise<T> {
  private final Object lock = new Object();
  private final List<Consumer<T>> consumers = new ArrayList<>();
  private Maybe<T> value;

  public PromisedValue() {
    this.value = none();
  }

  public PromisedValue(T value) {
    this.value = some(value);
  }

  @Override
  public void accept(T value) {
    synchronized (lock) {
      assertValueIsNotSetYet(value);
      this.value = some(value);
    }
    // From this point 'consumers' is effectively immutable as we don't change it
    // once 'value' is set so we can read its state outside of synchronized block to ensure
    // outside code is not called with lock held.
    consumers.forEach(listener -> listener.accept(value));
  }

  private void assertValueIsNotSetYet(T value) {
    if (this.value.isSome()) {
      throw new IllegalStateException(unlines(
          "Cannot set 'value' to: " + value, "as it is already set to: " + this.value.get()));
    }
  }

  @Override
  public T get() {
    synchronized (lock) {
      return value.get();
    }
  }

  @Override
  public Maybe<T> toMaybe() {
    synchronized (lock) {
      return value;
    }
  }

  @Override
  public void addConsumer(Consumer<T> consumer) {
    requireNonNull(consumer);
    boolean notify = false;
    synchronized (lock) {
      if (value.isSome()) {
        notify = true;
      } else {
        consumers.add(consumer);
      }
    }
    if (notify) {
      // Once value!=null it becomes effectively immutable.
      consumer.accept(value.get());
    }
  }

  // visible for testing
  boolean isCurrentThreadHoldingALock() {
    return Thread.holdsLock(lock);
  }

  @Override
  public <U> Promise<U> chain(Function<T, U> func) {
    PromisedValue<U> chained = new PromisedValue<>();
    addConsumer(v -> chained.accept(func.apply(v)));
    return chained;
  }
}
