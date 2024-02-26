package org.smoothbuild.run.step;

import org.smoothbuild.common.log.Log;

public interface StepReporter {
  public void startNewPhase(String name);

  public void report(Log log);
}
