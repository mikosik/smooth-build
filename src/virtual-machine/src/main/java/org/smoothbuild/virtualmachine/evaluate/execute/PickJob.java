package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.PickEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class PickJob extends OperationJob {
  private final BPick pick;

  public PickJob(JobContext jobContext, BPick pick, List<Job> environment, Trace trace) {
    super(jobContext, pick, environment, trace);
    this.pick = pick;
  }

  @Override
  protected OperationEvaluator createEvaluator() {
    return new PickEvaluator(pick, trace());
  }
}
