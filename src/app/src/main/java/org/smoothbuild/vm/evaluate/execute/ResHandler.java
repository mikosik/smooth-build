package org.smoothbuild.vm.evaluate.execute;

import java.util.function.Consumer;
import org.smoothbuild.common.concurrent.SoftTerminationExecutor;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.ComputationResult;
import org.smoothbuild.vm.evaluate.task.Task;

public class ResHandler implements Consumer1<ComputationResult, BytecodeException> {
  private final SoftTerminationExecutor executor;
  private final TaskReporter reporter;
  private final Consumer<ValueB> consumer;
  private final Task task;

  public ResHandler(
      Task task,
      SoftTerminationExecutor executor,
      TaskReporter reporter,
      Consumer<ValueB> consumer) {
    this.task = task;
    this.executor = executor;
    this.reporter = reporter;
    this.consumer = consumer;
  }

  @Override
  public void accept(ComputationResult result) throws BytecodeException {
    reporter.report(task, result);
    if (result.output().valueB() != null) {
      consumer.accept(result.output().valueB());
    } else {
      executor.terminate();
    }
  }
}
