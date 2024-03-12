package org.smoothbuild.app.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ReportMatcher;
import org.smoothbuild.common.log.Reporter;
import org.smoothbuild.common.log.ResultSource;

public class TaskFilteringReporter implements Reporter {
  private final Reporter reporter;
  private final ReportMatcher reportMatcher;

  public TaskFilteringReporter(Reporter reporter, ReportMatcher reportMatcher) {
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
