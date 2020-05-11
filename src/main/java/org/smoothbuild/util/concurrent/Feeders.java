package org.smoothbuild.util.concurrent;

import java.util.List;

public class Feeders {
  public static <T> void runWhenAllAvailable(List<? extends Feeder<T>> feeders, Runnable runnable) {
    ThresholdRunnable latch = new ThresholdRunnable(feeders.size(), runnable);
    feeders.forEach(child -> child.addConsumer(v -> latch.run()));
  }
}
