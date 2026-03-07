package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateTuple;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BCreateTupleEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BCreateTupleJob extends BOperationJob {
  private final BCreateTuple createTuple;

  public BCreateTupleJob(
      JobContext jobContext, BCreateTuple createTuple, List<Job> environment, Trace trace) {
    super(jobContext, createTuple, environment, trace);
    this.createTuple = createTuple;
  }

  @Override
  protected BOperationEvaluator<BCreateTuple> createEvaluator() {
    return new BCreateTupleEvaluator(createTuple, trace());
  }
}
