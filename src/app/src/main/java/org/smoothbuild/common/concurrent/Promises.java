package org.smoothbuild.common.concurrent;

import org.smoothbuild.common.collect.List;

public class Promises {
  public static <T> void runWhenAllAvailable(
      List<? extends Promise<T>> promises, Runnable runnable) {
    ThresholdRunnable latch = new ThresholdRunnable(promises.size(), runnable);
    promises.forEach(child -> child.addConsumer(v -> latch.run()));
  }
}
