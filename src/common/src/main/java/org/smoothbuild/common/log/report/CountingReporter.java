package org.smoothbuild.common.log.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.ResultSource;

public class CountingReporter implements Reporter {
  private final Reporter reporter;
  private final LogCounters logCounters;

  public CountingReporter(Reporter reporter, LogCounters logCounters) {
    this.reporter = reporter;
    this.logCounters = logCounters;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    for (Log log : logs) {
      logCounters.increment(log.level());
    }
    reporter.report(label, details, source, logs);
  }
}
