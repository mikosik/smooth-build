package org.smoothbuild.common.log.report;

import java.util.function.Predicate;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.base.Level;

public class FilteringReporter implements Reporter {
  private final ReportPrinter reporter;
  private final Predicate<Report> taskFilter;
  private final Predicate<Report> traceFilter;
  private final Level logLevel;

  public FilteringReporter(
      ReportPrinter reporter,
      Predicate<Report> taskFilter,
      Predicate<Report> traceFilter,
      Level logLevel) {
    this.reporter = reporter;
    this.taskFilter = taskFilter;
    this.traceFilter = traceFilter;
    this.logLevel = logLevel;
  }

  @Override
  public void submit(Report report) {
    if (taskFilter.test(report)) {
      var trace = traceFilter.test(report) ? report.trace() : Maybe.<Trace>none();
      var logs = report.logs().filter(l -> l.level().hasPriorityAtLeast(logLevel));
      reporter.print(report.label(), trace, report.origin(), logs);
    }
  }
}
