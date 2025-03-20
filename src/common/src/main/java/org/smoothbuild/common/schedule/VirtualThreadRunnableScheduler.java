package org.smoothbuild.common.schedule;

import static java.lang.Thread.startVirtualThread;

public class VirtualThreadRunnableScheduler implements RunnableScheduler {
  @Override
  public void submit(Runnable runnable) {
    startVirtualThread(runnable);
  }
}
