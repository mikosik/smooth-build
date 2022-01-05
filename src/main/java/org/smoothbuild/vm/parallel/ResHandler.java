package org.smoothbuild.vm.parallel;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.Computed;
import org.smoothbuild.vm.job.job.JobInfo;

public class ResHandler implements Consumer<Computed> {
  private final JobInfo jobInfo;
  private final Consumer<ValB> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResHandler(JobInfo jobInfo, Consumer<ValB> consumer,
      ExecutionReporter reporter, SoftTerminationExecutor jobExecutor) {
    this.jobInfo = jobInfo;
    this.consumer = consumer;
    this.reporter = reporter;
    this.jobExecutor = jobExecutor;
  }

  @Override
  public void accept(Computed computed) {
    reporter.report(jobInfo, computed);
    if (computed.hasOutputWithValue()) {
      consumer.accept(computed.output().val());
    } else {
      jobExecutor.terminate();
    }
  }
}
