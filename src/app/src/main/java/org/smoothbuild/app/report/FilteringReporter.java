package org.smoothbuild.app.report;

import static org.smoothbuild.common.log.Label.label;

import org.smoothbuild.app.run.eval.report.TaskMatcher;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Reporter;
import org.smoothbuild.common.log.ResultSource;

public class FilteringReporter implements Reporter {
  private static final Label EVALUATE = label("evaluate");
  private final Reporter reporter;
  private final TaskMatcher taskMatcher;

  public FilteringReporter(Reporter reporter, TaskMatcher taskMatcher) {
    this.reporter = reporter;
    this.taskMatcher = taskMatcher;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    if (taskMatcher.matches(label, logs) || !label.startsWith(EVALUATE)) {
      reporter.report(label, details, source, logs);
    }
  }
}
