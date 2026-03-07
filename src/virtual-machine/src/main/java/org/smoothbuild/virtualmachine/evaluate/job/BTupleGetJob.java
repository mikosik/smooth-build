package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BTupleGetEvaluator;

public final class BTupleGetJob extends BOperationJob {
  private final BTupleGet tupleGet;

  public BTupleGetJob(
      JobContext jobContext, BTupleGet tupleGet, List<Job> environment, Trace trace) {
    super(jobContext, tupleGet, environment, trace);
    this.tupleGet = tupleGet;
  }

  @Override
  protected BOperationEvaluator<BTupleGet> createEvaluator() {
    return new BTupleGetEvaluator(tupleGet, trace());
  }
}
