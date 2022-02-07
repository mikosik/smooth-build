package org.smoothbuild.out.report;

import java.util.List;

import org.smoothbuild.out.log.Log;
import org.smoothbuild.vm.job.job.TaskInfo;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(TaskInfo taskInfo, List<Log> logs);
}
