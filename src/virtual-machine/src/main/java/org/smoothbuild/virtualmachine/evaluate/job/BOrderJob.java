package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOrderEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BOrderJob extends BOperationJob {
  private final BOrder order;

  public BOrderJob(JobContext jobContext, BOrder order, List<Job> environment, Trace trace) {
    super(jobContext, order, environment, trace);
    this.order = order;
  }

  @Override
  protected BOperationEvaluator<BOrder> createEvaluator() {
    return new BOrderEvaluator(order, trace());
  }
}
