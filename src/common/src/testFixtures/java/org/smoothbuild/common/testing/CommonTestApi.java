package org.smoothbuild.common.testing;

import org.smoothbuild.common.task.Scheduler;

public interface CommonTestApi {
  public Scheduler scheduler();

  public TestReporter reporter();
}
