package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BSelectEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;

public final class BSelectJob extends BOperationJob {
  private final BSelect pick;

  public BSelectJob(JobContext jobContext, BSelect pick, List<Job> environment, Trace trace) {
    super(jobContext, pick, environment, trace);
    this.pick = pick;
  }

  @Override
  protected OperationEvaluator createEvaluator() {
    return new BSelectEvaluator(pick, trace());
  }
}
