package org.smoothbuild.cli.console;

import java.util.List;

import org.smoothbuild.vm.job.job.TaskInfo;

public interface Reporter {
  void startNewPhase(String name);

  void reportTask(TaskInfo taskInfo, String taskHeader, List<Log> logs);

  void report(List<Log> logs);

  void report(String taskHeader, List<Log> logs);

  void printSummary();
}
