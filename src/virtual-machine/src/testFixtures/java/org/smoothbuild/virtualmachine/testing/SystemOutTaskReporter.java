package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct.messageSeverity;
import static org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct.messageText;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

class SystemOutTaskReporter implements TaskReporter {
  @Override
  public void report(Task task, ComputationResult result) throws BytecodeException {
    for (TupleB message : result.output().messages().elems(TupleB.class)) {
      System.out.println(messageSeverity(message) + " " + messageText(message));
    }
  }

  @Override
  public void reportEvaluationException(Throwable throwable) {
    System.out.println("reportEvaluationException: " + throwable.getMessage());
  }
}
