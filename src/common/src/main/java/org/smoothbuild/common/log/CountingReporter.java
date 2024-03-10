package org.smoothbuild.common.log;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.step.StepReporter;

public class CountingReporter implements StepReporter {
  private final StepReporter stepReporter;
  private final LogCounters logCounters;

  public CountingReporter(StepReporter stepReporter, LogCounters logCounters) {
    this.stepReporter = stepReporter;
    this.logCounters = logCounters;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    for (Log log : logs) {
      logCounters.increment(log.level());
    }
    stepReporter.report(label, details, source, logs);
  }
}
