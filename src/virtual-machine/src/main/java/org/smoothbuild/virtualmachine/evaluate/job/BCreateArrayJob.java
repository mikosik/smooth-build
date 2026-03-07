package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateArray;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BCreateArrayEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BCreateArrayJob extends BOperationJob {
  private final BCreateArray createArray;

  public BCreateArrayJob(
      JobContext jobContext, BCreateArray createArray, List<Job> environment, Trace trace) {
    super(jobContext, createArray, environment, trace);
    this.createArray = createArray;
  }

  @Override
  protected BOperationEvaluator<BCreateArray> createEvaluator() {
    return new BCreateArrayEvaluator(createArray, trace());
  }
}
