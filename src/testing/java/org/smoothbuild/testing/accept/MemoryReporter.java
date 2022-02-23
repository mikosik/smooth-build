package org.smoothbuild.testing.accept;

import java.util.List;

import org.smoothbuild.out.log.ImmutableLogs;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.job.TaskInfo;
import org.smoothbuild.vm.parallel.TaskReporter;

public class MemoryReporter implements Reporter, TaskReporter {
  private final LogBuffer buffer = new LogBuffer();

  @Override
  public void startNewPhase(String name) {
  }

  @Override
  public void report(TaskInfo taskInfo, String taskHeader, List<Log> logs) {
    buffer.logAll(logs);
  }

  @Override
  public void report(Log log) {
    buffer.log(log);
  }

  @Override
  public void report(String taskHeader, List<Log> logs) {
    buffer.logAll(logs);
  }

  @Override
  public void printSummary() {
  }

  public boolean containsProblems() {
    return buffer.containsProblem();
  }

  public ImmutableLogs logs() {
    return buffer.toImmutableLogs();
  }
}
