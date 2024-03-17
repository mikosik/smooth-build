package org.smoothbuild.common.testing;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;

public class MemoryReporter implements Reporter {
  private final Logger buffer = new Logger();

  @Override
  public void report(Report report) {
    buffer.logAll(report.logs());
  }

  public boolean containsFailure() {
    return buffer.containsFailure();
  }

  public List<Log> logs() {
    return buffer.toList();
  }
}
