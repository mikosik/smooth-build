package org.smoothbuild.testing.accept;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.out.report.Reporter;

public class MemoryReporter implements Reporter {
  private final Logger buffer = new Logger();

  @Override
  public void startNewPhase(String name) {}

  @Override
  public void report(Log log) {
    buffer.log(log);
  }

  @Override
  public void report(boolean visible, String taskHeader, List<Log> logs) {
    System.out.println("taskHeader = " + taskHeader);
    buffer.logAll(logs);
  }

  @Override
  public void printSummary() {}

  @Override
  public void reportResult(String string) {}

  public boolean containsFailure() {
    return buffer.containsFailure();
  }

  public List<Log> logs() {
    return buffer.toList();
  }
}
