package org.smoothbuild.app.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ResultSource;
import org.smoothbuild.common.step.StepReporter;

public interface Reporter extends StepReporter {
  @Override
  public default void report(Label label, String details, ResultSource source, List<Log> logs) {
    report(true, label, details, source, logs);
  }

  public void report(
      boolean visible, Label label, String details, ResultSource source, List<Log> logs);

  public void printSummary();

  public void reportResult(String string);
}
