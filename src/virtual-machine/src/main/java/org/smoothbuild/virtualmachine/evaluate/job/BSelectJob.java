package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BSelectEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;

public final class BSelectJob extends BOperationJob {
  private final BSelect select;

  public BSelectJob(JobContext jobContext, BSelect select, List<Job> environment, Trace trace) {
    super(jobContext, select, environment, trace);
    this.select = select;
  }

  @Override
  protected OperationEvaluator<BSelect> createEvaluator() {
    return new BSelectEvaluator(select, trace());
  }
}
