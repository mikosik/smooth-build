package org.smoothbuild.testing.accept;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.out.log.ImmutableLogs;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;

public class MemoryReporter implements Reporter {
  private final LogBuffer buffer = new LogBuffer();

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

  public ImmutableLogs logs() {
    return buffer.toImmutableLogs();
  }
}
