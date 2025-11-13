package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BCombineEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class CombineJob extends OperationJob {
  private final BCombine combine;

  public CombineJob(JobContext jobContext, BCombine combine, List<Job> environment, Trace trace) {
    super(jobContext, combine, environment, trace);
    this.combine = combine;
  }

  @Override
  protected BOperationEvaluator createEvaluator() {
    return new BCombineEvaluator(combine, trace());
  }
}
