package org.smoothbuild.common.concurrent;

import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;

/**
 * Parallel executor of Runnables. It terminates softly (finishing runnables that are being executed
 * but skipping those waiting in the queue) once {@link #terminate()} is called.
 *
 * This class is thread-safe.
 */
public class SoftTerminationExecutor {
  private final ThreadPoolExecutor executorService;

  public SoftTerminationExecutor(int threadCount) {
    this.executorService = createThreadPoolExecutor(threadCount);
  }

  private static ThreadPoolExecutor createThreadPoolExecutor(int threadCount) {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        threadCount, threadCount, 0L, MILLISECONDS, new LinkedBlockingQueue<>());
    executor.setRejectedExecutionHandler(new DiscardPolicy());
    return executor;
  }

  public void enqueue(Runnable runnable) {
    executorService.execute(runnable);
  }

  public void terminate() {
    executorService.shutdown();
    executorService.getQueue().clear();
  }

  public void awaitTermination() throws InterruptedException {
    executorService.awaitTermination(MAX_VALUE, DAYS);
  }
}
