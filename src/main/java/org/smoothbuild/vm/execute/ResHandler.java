package org.smoothbuild.vm.execute;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.ComputationResult;
import org.smoothbuild.vm.task.Task;

public class ResHandler implements Consumer<ComputationResult> {
  private final SoftTerminationExecutor executor;
  private final ExecutionReporter reporter;
  private final Consumer<InstB> consumer;
  private final Task task;

  public ResHandler(Task task, SoftTerminationExecutor executor, ExecutionReporter reporter,
      Consumer<InstB> consumer) {
    this.task = task;
    this.executor = executor;
    this.reporter = reporter;
    this.consumer = consumer;
  }

  @Override
  public void accept(ComputationResult result) {
    reporter.report(task, result);
    if (result.output().instB() != null) {
      consumer.accept(result.output().instB());
    } else {
      executor.terminate();
    }
  }
}
