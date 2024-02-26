package org.smoothbuild.run.eval.report;

import java.util.List;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.vm.evaluate.task.Task;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(Task task, List<Log> logs);
}
