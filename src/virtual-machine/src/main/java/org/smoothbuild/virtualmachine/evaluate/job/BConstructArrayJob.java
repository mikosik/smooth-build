package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructArray;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BConstructArrayEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BConstructArrayJob extends BOperationJob {
  private final BConstructArray constructArray;

  public BConstructArrayJob(
      JobContext jobContext, BConstructArray constructArray, List<Job> environment, Trace trace) {
    super(jobContext, constructArray, environment, trace);
    this.constructArray = constructArray;
  }

  @Override
  protected BOperationEvaluator<BConstructArray> createEvaluator() {
    return new BConstructArrayEvaluator(constructArray, trace());
  }
}
