package org.smoothbuild.common.concurrent;

import java.util.function.Consumer;
import java.util.function.Function;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;

public interface Promise<T> {
  public static <T> Promise<T> promise(T value) {
    return new ImmutablePromise<>(value);
  }

  public static <T> MutablePromise<T> promise() {
    return new MutablePromise<>();
  }

  public T get();

  public T getBlocking() throws InterruptedException;

  public Maybe<T> toMaybe();

  public void addConsumer(Consumer<T> consumer);

  public default <R> Promise<R> map(Function<T, R> mapper) {
    MutablePromise<R> promise = promise();
    addConsumer(v -> promise.accept(mapper.apply(v)));
    return promise;
  }

  public static void runWhenAllAvailable(List<? extends Promise<?>> promises, Runnable runnable) {
    ThresholdRunnable latch = new ThresholdRunnable(promises.size(), runnable);
    promises.forEach(child -> child.addConsumer(v -> latch.run()));
  }
}
