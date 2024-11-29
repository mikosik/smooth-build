package org.smoothbuild.common.log.report;

import org.smoothbuild.common.log.base.Level;

public class FilteringReporter implements Reporter {
  private final ReportPrinter reporter;
  private final ReportMatcher taskFilter;
  private final ReportMatcher traceFilter;
  private final Level logLevel;

  public FilteringReporter(
      ReportPrinter reporter, ReportMatcher taskFilter, ReportMatcher traceFilter, Level logLevel) {
    this.reporter = reporter;
    this.taskFilter = taskFilter;
    this.traceFilter = traceFilter;
    this.logLevel = logLevel;
  }

  @Override
  public void submit(Report report) {
    if (taskFilter.matches(report.label(), report.logs())) {
      var filteredTrace =
          traceFilter.matches(report.label(), report.logs()) ? report.trace() : new Trace();
      var filteredLogs = report.logs().filter(l -> l.level().hasPriorityAtLeast(logLevel));
      reporter.print(report.label(), filteredTrace, report.origin(), filteredLogs);
    }
  }
}
