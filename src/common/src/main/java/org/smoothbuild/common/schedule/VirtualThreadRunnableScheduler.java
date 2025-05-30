package org.smoothbuild.common.schedule;

import static java.lang.Thread.startVirtualThread;

import jakarta.inject.Inject;

public class VirtualThreadRunnableScheduler implements RunnableScheduler {
  @Inject
  public VirtualThreadRunnableScheduler() {}

  @Override
  public void submit(Runnable runnable) {
    startVirtualThread(runnable);
  }
}
