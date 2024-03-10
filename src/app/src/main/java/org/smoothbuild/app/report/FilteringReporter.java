package org.smoothbuild.app.report;

import jakarta.inject.Inject;
import org.smoothbuild.app.run.eval.report.TaskMatcher;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ResultSource;
import org.smoothbuild.common.step.StepReporter;

public class FilteringReporter implements StepReporter {
  private final StepReporter reporter;
  private final TaskMatcher taskMatcher;

  @Inject
  public FilteringReporter(StepReporter reporter, TaskMatcher taskMatcher) {
    this.reporter = reporter;
    this.taskMatcher = taskMatcher;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    if (taskMatcher.matches(label, logs)) {
      reporter.report(label, details, source, logs);
    }
  }
}
