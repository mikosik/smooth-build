package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask.JobContext;
import org.smoothbuild.virtualmachine.evaluate.evaluator.CombineEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;

public final class CombineJob extends OperationJob {
  private final BCombine combine;

  public CombineJob(JobContext jobContext, BCombine combine, List<Job> environment, Trace trace) {
    super(jobContext, combine, environment, trace);
    this.combine = combine;
  }

  @Override
  protected OperationEvaluator createEvaluator() {
    return new CombineEvaluator(combine, trace());
  }
}
