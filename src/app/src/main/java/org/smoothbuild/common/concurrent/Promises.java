package org.smoothbuild.common.concurrent;

import io.vavr.collection.Traversable;

public class Promises {
  public static <T> void runWhenAllAvailable(
      Traversable<? extends Promise<T>> promises, Runnable runnable) {
    ThresholdRunnable latch = new ThresholdRunnable(promises.size(), runnable);
    promises.forEach(child -> child.addConsumer(v -> latch.run()));
  }
}
