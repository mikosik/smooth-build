package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask.JobContext;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BPickEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;

public final class BPickJob extends BOperationJob {
  private final BPick pick;

  public BPickJob(JobContext jobContext, BPick pick, List<Job> environment, Trace trace) {
    super(jobContext, pick, environment, trace);
    this.pick = pick;
  }

  @Override
  protected OperationEvaluator createEvaluator() {
    return new BPickEvaluator(pick, trace());
  }
}
