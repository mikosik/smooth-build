package org.smoothbuild.common.log;

import org.smoothbuild.common.collect.List;

public class FilteringReporter implements Reporter {
  private final Reporter reporter;
  private final ReportMatcher reportMatcher;

  public FilteringReporter(Reporter reporter, ReportMatcher reportMatcher) {
    this.reporter = reporter;
    this.reportMatcher = reportMatcher;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    if (reportMatcher.matches(label, logs)) {
      reporter.report(label, details, source, logs);
    }
  }
}
