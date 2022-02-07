package org.smoothbuild.out.report;

import java.util.List;

import org.smoothbuild.out.log.Log;

public interface Reporter {
  void startNewPhase(String name);

  void report(List<Log> logs);

  void report(String taskHeader, List<Log> logs);

  void printSummary();
}
