package org.smooth.build.common.step;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.step.StepReporter;

public class MemoryStepReporter implements StepReporter {
  private final Logger buffer = new Logger();

  @Override
  public void startNewPhase(String name) {}

  @Override
  public void report(Log log) {
    buffer.log(log);
  }

  public boolean containsFailure() {
    return buffer.containsFailure();
  }

  public List<Log> logs() {
    return buffer.toList();
  }
}
