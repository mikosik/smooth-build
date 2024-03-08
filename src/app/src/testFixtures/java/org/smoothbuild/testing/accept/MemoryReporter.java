package org.smoothbuild.testing.accept;

import org.smoothbuild.app.report.Reporter;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ResultSource;
import org.smoothbuild.common.testing.MemoryStepReporter;

public class MemoryReporter extends MemoryStepReporter implements Reporter {
  @Override
  public void report(
      boolean visible, Label label, String details, ResultSource source, List<Log> logs) {
    System.out.println("taskHeader = " + label);
    report(label, details, source, logs);
  }

  @Override
  public void printSummary() {}

  @Override
  public void reportResult(String string) {}
}
