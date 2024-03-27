package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.storedLogLevel;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.storedLogMessage;

import java.io.PrintWriter;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

class SystemOutTaskReporter implements TaskReporter {
  private final PrintWriter printWriter;

  public SystemOutTaskReporter(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  @Override
  public void report(Task task, ComputationResult result) throws BytecodeException {
    for (BTuple message : result.output().storedLogs().elements(BTuple.class)) {
      printWriter.println(storedLogLevel(message).toJavaString() + " "
          + storedLogMessage(message).toJavaString());
    }
  }

  @Override
  public void reportEvaluationException(Throwable throwable) {
    printWriter.println("reportEvaluationException: " + throwable.getMessage());
  }
}
