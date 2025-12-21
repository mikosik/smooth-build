package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BChooseEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BChooseJob extends BOperationJob {
  private final BChoose choose;

  public BChooseJob(JobContext jobContext, BChoose choose, List<Job> environment, Trace trace) {
    super(jobContext, choose, environment, trace);
    this.choose = choose;
  }

  @Override
  protected BOperationEvaluator<BChoose> createEvaluator() {
    return new BChooseEvaluator(choose, trace());
  }
}
