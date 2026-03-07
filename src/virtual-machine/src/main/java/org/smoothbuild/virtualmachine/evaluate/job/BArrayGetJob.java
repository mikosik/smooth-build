package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BArrayGetEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BArrayGetJob extends BOperationJob {
  private final BArrayGet arrayGet;

  public BArrayGetJob(
      JobContext jobContext, BArrayGet arrayGet, List<Job> environment, Trace trace) {
    super(jobContext, arrayGet, environment, trace);
    this.arrayGet = arrayGet;
  }

  @Override
  protected BOperationEvaluator<BArrayGet> createEvaluator() {
    return new BArrayGetEvaluator(arrayGet, trace());
  }
}
