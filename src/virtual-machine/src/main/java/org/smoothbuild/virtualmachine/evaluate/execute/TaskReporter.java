package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public interface TaskReporter {
  public void report(Task task, ComputationResult result) throws BytecodeException;

  public void reportEvaluationException(Throwable throwable);
}
