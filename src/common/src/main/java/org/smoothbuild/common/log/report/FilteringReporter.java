package org.smoothbuild.common.log.report;

public class FilteringReporter implements Reporter {
  private final Reporter reporter;
  private final ReportMatcher reportMatcher;

  public FilteringReporter(Reporter reporter, ReportMatcher reportMatcher) {
    this.reporter = reporter;
    this.reportMatcher = reportMatcher;
  }

  @Override
  public void submit(Report report) {
    if (reportMatcher.matches(report.label(), report.logs())) {
      reporter.submit(report);
    }
  }
}
