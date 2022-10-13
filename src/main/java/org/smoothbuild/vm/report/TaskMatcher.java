package org.smoothbuild.vm.report;

import java.util.List;

import org.smoothbuild.out.log.Log;
import org.smoothbuild.vm.execute.TaskInfo;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(TaskInfo taskInfo, List<Log> logs);
}
