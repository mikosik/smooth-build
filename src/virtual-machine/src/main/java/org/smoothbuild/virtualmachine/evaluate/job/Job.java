package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.schedule.Output.failedOutput;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.virtualmachine.VmConfig;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.base.BRefInliner;
import org.smoothbuild.virtualmachine.evaluate.base.DebugSymbols;
import org.smoothbuild.virtualmachine.evaluate.cache.CachingOperatorEvaluator;

public abstract sealed class Job permits BLambdaJob, SchedulingJob, BValueJob {
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

  public BytecodeFactory bytecodeFactory() {
    return jobContext.bytecodeFactory();
  }

  public BRefInliner refInliner() {
    return jobContext.refInliner();
  }

  public CachingOperatorEvaluator cachingOperatorEvaluator() {
    return jobContext.cachingOperatorEvaluator();
  }

  public Map<Hash, DebugSymbols> debugSymbols() {
    return jobContext.debugSymbols();
  }

  public Scheduler scheduler() {
    return jobContext.scheduler();
  }

  public VmConfig vmConfig() {
    return jobContext.vmConfig();
  }
}
