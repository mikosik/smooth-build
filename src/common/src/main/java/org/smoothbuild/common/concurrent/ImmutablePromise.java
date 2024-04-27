package org.smoothbuild.common.concurrent;

import static org.smoothbuild.common.collect.Maybe.some;

import java.util.function.Consumer;
import org.smoothbuild.common.collect.Maybe;

/**
 * Immutable implementation of {@link Promise}.
 * This class is thread-safe.
 */
public class ImmutablePromise<T> implements Promise<T> {
  private final T value;

  public ImmutablePromise(T value) {
    this.value = value;
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public T getBlocking() {
    return value;
  }

  @Override
  public Maybe<T> toMaybe() {
    return some(value);
  }

  @Override
  public void addConsumer(Consumer<T> consumer) {
    consumer.accept(value);
  }
}
