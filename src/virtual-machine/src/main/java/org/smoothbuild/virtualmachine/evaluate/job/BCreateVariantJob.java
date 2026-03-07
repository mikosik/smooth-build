package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateVariant;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BCreateVariantEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BCreateVariantJob extends BOperationJob {
  private final BCreateVariant createVariant;

  public BCreateVariantJob(
      JobContext jobContext, BCreateVariant createVariant, List<Job> environment, Trace trace) {
    super(jobContext, createVariant, environment, trace);
    this.createVariant = createVariant;
  }

  @Override
  protected BOperationEvaluator<BCreateVariant> createEvaluator() {
    return new BCreateVariantEvaluator(createVariant, trace());
  }
}
