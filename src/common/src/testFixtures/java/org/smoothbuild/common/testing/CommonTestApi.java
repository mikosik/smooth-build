package org.smoothbuild.common.testing;

import org.smoothbuild.common.task.Scheduler;

public abstract class CommonTestApi {
  public abstract Scheduler scheduler();

  public abstract TestReporter reporter();
}
