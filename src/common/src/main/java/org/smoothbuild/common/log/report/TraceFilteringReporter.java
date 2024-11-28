package org.smoothbuild.common.log.report;

public class TraceFilteringReporter implements Reporter {
  private final Reporter reporter;
  private final ReportMatcher traceFilter;

  public TraceFilteringReporter(Reporter reporter, ReportMatcher traceFilter) {
    this.reporter = reporter;
    this.traceFilter = traceFilter;
  }

  @Override
  public void submit(Report report) {
    if (traceFilter.matches(report.label(), report.logs())) {
      reporter.submit(report);
    } else {
      reporter.submit(report.withTrace(new Trace()));
    }
  }
}
