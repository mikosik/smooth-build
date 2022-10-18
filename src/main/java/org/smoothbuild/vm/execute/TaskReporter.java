package org.smoothbuild.vm.execute;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.report.TaskMatcher;
import org.smoothbuild.vm.task.Task;

public class TaskReporter {
  private final TaskMatcher taskMatcher;
  private final Reporter reporter;

  @Inject
  public TaskReporter(TaskMatcher taskMatcher, Reporter reporter) {
    this.taskMatcher = taskMatcher;
    this.reporter = reporter;
  }

  public void report(Task task, String taskHeader, List<Log> logs) {
    boolean visible = taskMatcher.matches(task, logs);
    reporter.report(visible, taskHeader, logs);
  }
}
