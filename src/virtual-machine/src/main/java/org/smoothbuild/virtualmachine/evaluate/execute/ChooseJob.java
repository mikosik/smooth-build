package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BChooseEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class ChooseJob extends OperationJob {
  private final BChoose choose;

  public ChooseJob(JobContext jobContext, BChoose choose, List<Job> environment, Trace trace) {
    super(jobContext, choose, environment, trace);
    this.choose = choose;
  }

  @Override
  protected BOperationEvaluator createEvaluator() {
    return new BChooseEvaluator(choose, trace());
  }
}
