package org.smoothbuild.common.log.report;

public class CountingReporter implements Reporter {
  private final Reporter reporter;
  private final LogCounters logCounters;

  public CountingReporter(Reporter reporter, LogCounters logCounters) {
    this.reporter = reporter;
    this.logCounters = logCounters;
  }

  @Override
  public void submit(Report report) {
    report.logs().forEach(log -> logCounters.increment(log.level()));
    reporter.submit(report);
  }
}
