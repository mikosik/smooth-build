package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BCombineEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BCombineJob extends BOperationJob {
  private final BCombine combine;

  public BCombineJob(JobContext jobContext, BCombine combine, List<Job> environment, Trace trace) {
    super(jobContext, combine, environment, trace);
    this.combine = combine;
  }

  @Override
  protected BOperationEvaluator<BCombine> createEvaluator() {
    return new BCombineEvaluator(combine, trace());
  }
}
