package org.smoothbuild.vm.report;

import java.util.List;

import org.smoothbuild.out.log.Log;
import org.smoothbuild.vm.task.Task;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(Task task, List<Log> logs);
}
