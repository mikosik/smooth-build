package org.smoothbuild.vm.execute;

import java.util.List;

import org.smoothbuild.out.log.Log;

public interface TaskReporter {
  public void report(TaskInfo taskInfo, String taskHeader, List<Log> logs);
}
