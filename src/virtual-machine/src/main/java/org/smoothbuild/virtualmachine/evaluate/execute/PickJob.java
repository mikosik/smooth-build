package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BPickEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class PickJob extends OperationJob {
  private final BPick pick;

  public PickJob(JobContext jobContext, BPick pick, List<Job> environment, Trace trace) {
    super(jobContext, pick, environment, trace);
    this.pick = pick;
  }

  @Override
  protected BOperationEvaluator createEvaluator() {
    return new BPickEvaluator(pick, trace());
  }
}
