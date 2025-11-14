package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.evaluate.evaluator.ChooseEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class ChooseJob extends OperationJob {
  private final BChoose choose;

  public ChooseJob(JobContext jobContext, BChoose choose, List<Job> environment, Trace trace) {
    super(jobContext, choose, environment, trace);
    this.choose = choose;
  }

  @Override
  protected OperationEvaluator createEvaluator() {
    return new ChooseEvaluator(choose, trace());
  }
}
