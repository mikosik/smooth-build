package org.smoothbuild.vm.execute;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.CompRes;
import org.smoothbuild.vm.task.Task;

public class ResHandler implements Consumer<CompRes> {
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
  public void accept(CompRes compRes) {
    reporter.report(task, compRes);
    if (compRes.hasOutputWithValue()) {
      consumer.accept(compRes.output().instB());
    } else {
      executor.terminate();
    }
  }
}
