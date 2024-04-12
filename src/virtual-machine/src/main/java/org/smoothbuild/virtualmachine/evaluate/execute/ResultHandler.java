package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.virtualmachine.evaluate.execute.TaskReport.taskReport;

import java.util.function.Consumer;
import org.smoothbuild.common.concurrent.SoftTerminationExecutor;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class ResultHandler implements Consumer1<ComputationResult, BytecodeException> {
  private final SoftTerminationExecutor executor;
  private final TaskReporter taskReporter;
  private final Consumer<BValue> consumer;
  private final Task task;

  public ResultHandler(
      Task task,
      SoftTerminationExecutor executor,
      TaskReporter taskReporter,
      Consumer<BValue> consumer) {
    this.task = task;
    this.executor = executor;
    this.taskReporter = taskReporter;
    this.consumer = consumer;
  }

  @Override
  public void accept(ComputationResult result) throws BytecodeException {
    taskReporter.report(taskReport(task, result));
    if (result.output().value() != null) {
      consumer.accept(result.output().value());
    } else {
      executor.terminate();
    }
  }
}
