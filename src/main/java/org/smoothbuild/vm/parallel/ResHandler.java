package org.smoothbuild.vm.parallel;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.CompRes;
import org.smoothbuild.vm.job.TaskInfo;

public class ResHandler implements Consumer<CompRes> {
  private final TaskInfo taskInfo;
  private final Consumer<ValB> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResHandler(TaskInfo taskInfo, Consumer<ValB> consumer,
      ExecutionReporter reporter, SoftTerminationExecutor jobExecutor) {
    this.taskInfo = taskInfo;
    this.consumer = consumer;
    this.reporter = reporter;
    this.jobExecutor = jobExecutor;
  }

  @Override
  public void accept(CompRes compRes) {
    reporter.report(taskInfo, compRes);
    if (compRes.hasOutputWithValue()) {
      consumer.accept(compRes.output().valB());
    } else {
      jobExecutor.terminate();
    }
  }
}
