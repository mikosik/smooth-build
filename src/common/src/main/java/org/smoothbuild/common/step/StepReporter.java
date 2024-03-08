package org.smoothbuild.common.step;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ResultSource;

public interface StepReporter {
  public void report(Label label, String details, ResultSource source, List<Log> logs);
}
