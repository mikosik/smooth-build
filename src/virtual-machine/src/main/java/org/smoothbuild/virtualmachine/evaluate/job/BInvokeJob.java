package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BInvokeEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;

public final class BInvokeJob extends BOperationJob {
  private final BInvoke invoke;

  public BInvokeJob(JobContext jobContext, BInvoke invoke, List<Job> environment, Trace trace) {
    super(jobContext, invoke, environment, trace);
    this.invoke = invoke;
  }

  @Override
  protected BOperationEvaluator<BInvoke> createEvaluator() {
    return new BInvokeEvaluator(invoke, trace());
  }
}
