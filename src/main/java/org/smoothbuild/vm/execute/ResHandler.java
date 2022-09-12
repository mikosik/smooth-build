package org.smoothbuild.vm.execute;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.CompRes;

public class ResHandler implements Consumer<CompRes> {
  private final TaskInfo taskInfo;
  private final SoftTerminationExecutor executor;
  private final ExecutionReporter reporter;
  private final Consumer<ValB> consumer;

  public ResHandler(TaskInfo taskInfo, SoftTerminationExecutor executor, ExecutionReporter reporter,
      Consumer<ValB> consumer) {
    this.taskInfo = taskInfo;
    this.executor = executor;
    this.reporter = reporter;
    this.consumer = consumer;
  }

  @Override
  public void accept(CompRes compRes) {
    reporter.report(taskInfo, compRes);
    if (compRes.hasOutputWithValue()) {
      consumer.accept(compRes.output().valB());
    } else {
      executor.terminate();
    }
  }
}
