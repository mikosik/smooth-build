package org.smoothbuild.common.log.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.Log;

public class LogFilteringReporter implements Reporter {
  private final Reporter reporter;
  private final Level level;

  public LogFilteringReporter(Reporter reporter, Level level) {
    this.reporter = reporter;
    this.level = level;
  }

  @Override
  public void submit(Report report) {
    reporter.submit(report.mapLogs(this::filter));
  }

  private List<Log> filter(List<Log> logs) {
    return logs.filter(l -> l.level().hasPriorityAtLeast(level));
  }
}
