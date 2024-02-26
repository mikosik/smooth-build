package org.smoothbuild.out.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.run.step.StepReporter;

public interface Reporter extends StepReporter {
  @Override
  public void startNewPhase(String name);

  @Override
  public void report(Log log);

  public default void report(String header, List<Log> logs) {
    report(true, header, logs);
  }

  public void report(boolean visible, String header, List<Log> logs);

  public void printSummary();

  public void reportResult(String string);
}
