package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructVariant;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BConstructVariantEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BConstructVariantJob extends BOperationJob {
  private final BConstructVariant constructVariant;

  public BConstructVariantJob(
      JobContext jobContext,
      BConstructVariant constructVariant,
      List<Job> environment,
      Trace trace) {
    super(jobContext, constructVariant, environment, trace);
    this.constructVariant = constructVariant;
  }

  @Override
  protected BOperationEvaluator<BConstructVariant> createEvaluator() {
    return new BConstructVariantEvaluator(constructVariant, trace());
  }
}
