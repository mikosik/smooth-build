package org.smoothbuild.common.schedule;

public interface RunnableScheduler {
  public void submit(Runnable runnable);
}
