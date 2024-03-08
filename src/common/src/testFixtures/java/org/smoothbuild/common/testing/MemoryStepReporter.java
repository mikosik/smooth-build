package org.smoothbuild.common.testing;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.log.ResultSource;
import org.smoothbuild.common.step.StepReporter;

public class MemoryStepReporter implements StepReporter {
  private final Logger buffer = new Logger();

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    buffer.logAll(logs);
  }

  public boolean containsFailure() {
    return buffer.containsFailure();
  }

  public List<Log> logs() {
    return buffer.toList();
  }
}
