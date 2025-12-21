package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.evaluator.OperationEvaluator;

public abstract sealed class BOperationJob extends SchedulingJob
    permits BChooseJob, BCombineJob, BInvokeJob, BOrderJob, BPickJob, BSelectJob {
  private final BOperation operation;

  public BOperationJob(
      JobContext jobContext, BOperation operation, List<Job> environment, Trace trace) {
    super(jobContext, operation, environment, trace);
    this.operation = operation;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var subExprResults = operation.subExprs().map(this::job).map(Job::evaluate);
    return scheduler().submit(this::evaluate, subExprResults);
  }

  private Output<BValue> evaluate(List<BValue> bValues) {
    return cachingOperatorEvaluator().evaluate(createEvaluator(), bValues);
  }

  protected abstract OperationEvaluator<?> createEvaluator();
}
