package org.smoothbuild.common.concurrent;

import java.util.Collection;

public class Promises {
  public static <T> void runWhenAllAvailable(Collection<? extends Promise<T>> promises,
      Runnable runnable) {
    ThresholdRunnable latch = new ThresholdRunnable(promises.size(), runnable);
    promises.forEach(child -> child.addConsumer(v -> latch.run()));
  }
}
