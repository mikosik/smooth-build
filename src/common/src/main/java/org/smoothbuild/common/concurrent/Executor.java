package org.smoothbuild.common.concurrent;

import static java.lang.Thread.startVirtualThread;
import static java.util.Objects.requireNonNull;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Executes submitted Runnables in separate thread. New virtual thread is created for each
 * submitted Runnable. Number of threads running in parallel at any given time is limited
 * to maxThreads passed to constructor.
 * This class is thread-safe.
 */
public class Executor {
  private final int maxThreads;
  private final Object lock;
  private int runningThreadsCount;
  private final Deque<Runnable> queue;

  public Executor(int maxThreads) {
    this.maxThreads = maxThreads;
    this.lock = new Object();
    this.runningThreadsCount = 0;
    this.queue = new LinkedList<>();
  }

  public void submit(Runnable runnable) {
    requireNonNull(runnable);
    if (takePermitOrAddToQueue(runnable)) {
      startThread(runnable);
    }
  }

  private void startThread(Runnable runnable) {
    startVirtualThread(runnableWrapper(runnable));
  }

  private Runnable runnableWrapper(Runnable runnable) {
    return () -> {
      try {
        runnable.run();
      } finally {
        onThreadCompleted();
      }
    };
  }

  private void onThreadCompleted() {
    var next = pollNextRunnable();
    if (next != null) {
      startThread(next);
    }
  }

  public void waitUntilIdle() throws InterruptedException {
    synchronized (lock) {
      while (!isIdle()) {
        lock.wait();
      }
    }
  }

  private boolean takePermitOrAddToQueue(Runnable runnable) {
    synchronized (lock) {
      if (runningThreadsCount < maxThreads) {
        runningThreadsCount++;
        return true;
      } else {
        queue.addLast(runnable);
        return false;
      }
    }
  }

  private Runnable pollNextRunnable() {
    synchronized (lock) {
      if (queue.isEmpty()) {
        runningThreadsCount--;
        if (isIdle()) {
          lock.notifyAll();
        }
        return null;
      } else {
        return queue.removeFirst();
      }
    }
  }

  private boolean isIdle() {
    return runningThreadsCount == 0 && queue.isEmpty();
  }
}
