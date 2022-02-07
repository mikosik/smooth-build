package org.smoothbuild.vm.parallel;

import java.util.List;

import org.smoothbuild.out.log.Log;
import org.smoothbuild.vm.job.job.TaskInfo;

public interface TaskReporter {
  public void report(TaskInfo taskInfo, String taskHeader, List<Log> logs);
}
