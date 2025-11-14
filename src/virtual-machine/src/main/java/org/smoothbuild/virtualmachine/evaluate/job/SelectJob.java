package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.SelectEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class SelectJob extends OperationJob {
  private final BSelect pick;

  public SelectJob(JobContext jobContext, BSelect pick, List<Job> environment, Trace trace) {
    super(jobContext, pick, environment, trace);
    this.pick = pick;
  }

  @Override
  protected OperationEvaluator createEvaluator() {
    return new SelectEvaluator(pick, trace());
  }
}
