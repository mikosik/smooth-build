package org.smoothbuild.out.report;

import java.util.List;

import org.smoothbuild.out.log.Log;

public interface Reporter {
  public void startNewPhase(String name);

  public void report(List<Log> logs);

  public void report(String taskHeader, List<Log> logs);

  public void printSummary();
}
