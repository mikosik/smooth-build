package org.smoothbuild.testing.accept;

import org.smoothbuild.app.report.Reporter;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.testing.MemoryStepReporter;

public class MemoryReporter extends MemoryStepReporter implements Reporter {
  @Override
  public void report(boolean visible, String taskHeader, List<Log> logs) {
    System.out.println("taskHeader = " + taskHeader);
    logs.forEach(this::report);
  }

  @Override
  public void printSummary() {}

  @Override
  public void reportResult(String string) {}
}
