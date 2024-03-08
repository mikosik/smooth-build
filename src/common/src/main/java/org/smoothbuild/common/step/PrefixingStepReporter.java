package org.smoothbuild.common.step;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ResultSource;

public class PrefixingStepReporter implements StepReporter {
  private final StepReporter stepReporter;
  private final Label prefix;

  public PrefixingStepReporter(StepReporter stepReporter, Label prefix) {
    this.stepReporter = stepReporter;
    this.prefix = prefix;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    stepReporter.report(prefix.append(label), details, source, logs);
  }
}
