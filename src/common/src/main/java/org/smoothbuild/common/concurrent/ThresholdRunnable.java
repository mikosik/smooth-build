package org.smoothbuild.common.concurrent;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is thread-safe.
 * Runnable is called without any lock held.
 */
public class ThresholdRunnable implements Runnable {
  private final AtomicInteger count;
  private final Runnable runnable;

  public ThresholdRunnable(int count, Runnable runnable) {
    this.count = new AtomicInteger(validateCount(count));
    this.runnable = requireNonNull(runnable);
    if (count == 0) {
      runnable.run();
    }
  }

  private int validateCount(int count) {
    if (count < 0) {
      throw new IllegalArgumentException(
          "'count' argument is " + count + " but should be 0 or more.");
    }
    return count;
  }

  @Override
  public void run() {
    if (count.decrementAndGet() == 0) {
      runnable.run();
    }
  }
}
