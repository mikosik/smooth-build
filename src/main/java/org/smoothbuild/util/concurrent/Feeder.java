package org.smoothbuild.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is thread-safe.
 */
public class Feeder<T> implements Consumer<T> {
  private final Object lock = new Object();
  private final List<Consumer<T>> consumers = new ArrayList<>();
  private T value;

  @Override
  public void accept(T value) {
    checkNotNull(value);
    synchronized (lock) {
      assertValueIsNotSetYet(value);
      this.value = value;
    }
    // From this point 'consumers' is effectively immutable as Feeder code doesn't change it
    // once 'value' is set so we can read its state outside of synchronized block to ensure
    // outside code is not called with lock held.
    consumers.forEach(listener -> listener.accept(value));
  }

  private void assertValueIsNotSetYet(T value) {
    if (this.value != null) {
      throw new IllegalStateException(unlines(
          "Cannot set 'value' to: " + value,
          "as it is already set to: " + this.value
      ));
    }
  }

  public T value() {
    synchronized (lock) {
      return value;
    }
  }

  public void addConsumer(Consumer<T> consumer) {
    checkNotNull(consumer);
    boolean notify = false;
    synchronized (lock) {
      if (value != null) {
        notify = true;
      } else {
        consumers.add(consumer);
      }
    }
    if (notify) {
      // Once value!=null it becomes effectively immutable.
      consumer.accept(value);
    }
  }

  // visible for testing
  boolean isCurrentThreadHoldingALock() {
    return Thread.holdsLock(lock);
  }
}
