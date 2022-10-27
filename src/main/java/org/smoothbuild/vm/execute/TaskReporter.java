package org.smoothbuild.vm.execute;

import org.smoothbuild.vm.compute.ComputationResult;
import org.smoothbuild.vm.task.Task;

public interface TaskReporter {
  void report(Task task, ComputationResult result);
}
