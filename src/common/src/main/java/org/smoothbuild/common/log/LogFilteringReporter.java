package org.smoothbuild.common.log;

import org.smoothbuild.common.collect.List;

public class LogFilteringReporter implements Reporter {
  private final Reporter reporter;
  private final Level level;

  public LogFilteringReporter(Reporter reporter, Level level) {
    this.reporter = reporter;
    this.level = level;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    var filtered = logs.filter(l -> l.level().hasPriorityAtLeast(level));
    reporter.report(label, details, source, filtered);
  }
}
