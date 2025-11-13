package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.evaluate.evaluator.InvokeEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class InvokeJob extends OperationJob {
  private final BInvoke invoke;

  public InvokeJob(JobContext jobContext, BInvoke invoke, List<Job> environment, Trace trace) {
    super(jobContext, invoke, environment, trace);
    this.invoke = invoke;
  }

  @Override
  protected OperationEvaluator createEvaluator() {
    return new InvokeEvaluator(invoke, trace());
  }
}
