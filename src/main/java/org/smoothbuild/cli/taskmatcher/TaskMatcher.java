package org.smoothbuild.cli.taskmatcher;

import java.util.List;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.vm.job.job.TaskInfo;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(TaskInfo taskInfo, List<Log> logs);
}
