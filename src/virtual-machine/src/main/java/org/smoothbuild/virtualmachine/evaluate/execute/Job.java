package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.failedOutput;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.CachingOperatorEvaluator;
import org.smoothbuild.virtualmachine.evaluate.evaluator.BOperationEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public abstract sealed class Job permits LambdaJob, SchedulingJob, ValueJob {
  private final JobContext jobContext;
  private final BExpr expr;
  private final List<Job> environment;
  private final Trace trace;

  public Job(JobContext jobContext, BExpr expr, List<Job> environment, Trace trace) {
    this.jobContext = jobContext;
    this.expr = expr;
    this.environment = environment;
    this.trace = trace;
  }

  public BExpr expr() {
    return expr;
  }

  public List<Job> environment() {
    return environment;
  }

  public Trace trace() {
    return trace;
  }

  public abstract Promise<Maybe<BValue>> evaluate();

  protected <T> Output<T> failedSchedulingOutput(Label label, Trace trace, Throwable e) {
    return failedOutput(label, some(trace), "Scheduling task failed with exception:", e);
  }

  protected Promise<Maybe<BValue>> evaluate(BExpr bExpr) throws BytecodeException {
    return job(bExpr).evaluate();
  }

  protected Job job(BExpr bExpr) {
    return job(bExpr, this);
  }

  protected Job job(BExpr expr, Job parentJob) {
    return job(expr, parentJob.environment(), parentJob.trace());
  }

  protected Job job(BExpr expr, List<Job> environment, Trace trace) {
    return jobContext.newJob(expr, environment, trace);
  }

  public BExpr call(BExpr lambdaExpr, List<BValue> arguments) throws BytecodeException {
    return bytecodeFactory().call(lambdaExpr, bytecodeFactory().tuple(arguments));
  }

  protected Output<BValue> outputForException(BOperationEvaluator evaluator, Exception e) {
    var fatal = fatal("Vm evaluation Task failed with exception:", e);
    return output(report(VM_EVALUATE, evaluator.trace(), list(fatal)));
  }

  public BytecodeFactory bytecodeFactory() {
    return jobContext.bytecodeFactory();
  }

  public BReferenceInliner referenceInliner() {
    return jobContext.referenceInliner();
  }

  public CachingOperatorEvaluator cachingOperatorEvaluator() {
    return jobContext.cachingOperatorEvaluator();
  }

  public BExprAttributes exprAttributes() {
    return jobContext.exprAttributes();
  }

  public Scheduler scheduler() {
    return jobContext.scheduler();
  }
}
