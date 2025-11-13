package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOrderEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class OrderJob extends OperationJob {
  private final BOrder order;

  public OrderJob(JobContext jobContext, BOrder order, List<Job> environment, Trace trace) {
    super(jobContext, order, environment, trace);
    this.order = order;
  }

  @Override
  protected BOperationEvaluator createEvaluator() {
    return new BOrderEvaluator(order, trace());
  }
}
