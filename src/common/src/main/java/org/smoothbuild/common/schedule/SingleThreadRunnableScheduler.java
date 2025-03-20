package org.smoothbuild.common.schedule;

import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class SingleThreadRunnableScheduler implements RunnableScheduler {
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  @Override
  public void submit(Runnable runnable) {
    executorService.submit(runnable);
  }
}
