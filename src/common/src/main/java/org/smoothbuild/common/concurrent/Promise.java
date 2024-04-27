package org.smoothbuild.common.concurrent;

import java.util.function.Consumer;
import org.smoothbuild.common.collect.Maybe;

public interface Promise<T> {
  public static <T> Promise<T> promise(T value) {
    return new ImmutablePromise<>(value);
  }

  public static <T> PromisedValue<T> promise() {
    return new PromisedValue<>();
  }

  public T get();

  public T getBlocking() throws InterruptedException;

  public Maybe<T> toMaybe();

  public void addConsumer(Consumer<T> consumer);
}
