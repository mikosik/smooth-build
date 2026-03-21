package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructTuple;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BConstructTupleEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BConstructTupleJob extends BOperationJob {
  private final BConstructTuple constructTuple;

  public BConstructTupleJob(
      JobContext jobContext, BConstructTuple constructTuple, List<Job> environment, Trace trace) {
    super(jobContext, constructTuple, environment, trace);
    this.constructTuple = constructTuple;
  }

  @Override
  protected BOperationEvaluator<BConstructTuple> createEvaluator() {
    return new BConstructTupleEvaluator(constructTuple, trace());
  }
}
