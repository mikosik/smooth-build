package org.smoothbuild.common.log.report;

import org.smoothbuild.common.log.base.Log;

public class CountingReporter implements Reporter {
  private final Reporter reporter;
  private final LogCounters logCounters;

  public CountingReporter(Reporter reporter, LogCounters logCounters) {
    this.reporter = reporter;
    this.logCounters = logCounters;
  }

  @Override
  public void submit(Report report) {
    for (Log log : report.logs()) {
      logCounters.increment(log.level());
    }
    reporter.submit(report);
  }
}
