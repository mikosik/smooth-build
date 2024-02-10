package org.smoothbuild.vm.evaluate.execute;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.evaluate.compute.ComputationResult;
import org.smoothbuild.vm.evaluate.task.Task;

public interface TaskReporter {
  public void report(Task task, ComputationResult result) throws BytecodeException;
}
