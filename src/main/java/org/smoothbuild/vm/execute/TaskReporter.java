package org.smoothbuild.vm.execute;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.report.TaskMatcher;

public class TaskReporter {
  private final TaskMatcher taskMatcher;
  private final Reporter reporter;

  @Inject
  public TaskReporter(TaskMatcher taskMatcher, Reporter reporter) {
    this.taskMatcher = taskMatcher;
    this.reporter = reporter;
  }

  public void report(TaskInfo taskInfo, String taskHeader, List<Log> logs) {
    boolean visible = taskMatcher.matches(taskInfo, logs);
    reporter.report(visible, taskHeader, logs);
  }
}
