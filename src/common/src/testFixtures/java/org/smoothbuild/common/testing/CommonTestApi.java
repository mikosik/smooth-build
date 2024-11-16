package org.smoothbuild.common.testing;

import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.task.Scheduler;

public interface CommonTestApi {
  public Scheduler scheduler();

  public TestReporter reporter();

  default Alias alias() {
    return alias("t-alias");
  }

  default Alias alias(String alias) {
    return Alias.alias(alias);
  }
}
