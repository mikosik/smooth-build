package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;
import static org.smoothbuild.virtualmachine.evaluate.execute.BTrace.bTrace;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.function.Function2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.task.Task2;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;
import org.smoothbuild.virtualmachine.evaluate.step.CombineStep;
import org.smoothbuild.virtualmachine.evaluate.step.InvokeStep;
import org.smoothbuild.virtualmachine.evaluate.step.OrderStep;
import org.smoothbuild.virtualmachine.evaluate.step.PickStep;
import org.smoothbuild.virtualmachine.evaluate.step.SelectStep;
import org.smoothbuild.virtualmachine.evaluate.step.Step;

/**
 * Evaluates BExpr.
 * This class is thread-safe.
 */
public class BEvaluate implements Task1<BExpr, BValue> {
  private final Scheduler scheduler;
  private final StepEvaluator stepEvaluator;
  private final BytecodeFactory bytecodeFactory;
  private final BReferenceInliner bReferenceInliner;

  @Inject
  public BEvaluate(
      Scheduler scheduler,
      StepEvaluator stepEvaluator,
      BytecodeFactory bytecodeFactory,
      BReferenceInliner bReferenceInliner) {
    this.scheduler = scheduler;
    this.stepEvaluator = stepEvaluator;
    this.bytecodeFactory = bytecodeFactory;
    this.bReferenceInliner = bReferenceInliner;
  }

  @Override
  public Output<BValue> execute(BExpr expr) {
    var label = VM_LABEL.append("schedule");
    try {
      return successOutput(scheduleJob(newJob(expr)), label, new BTrace());
    } catch (BytecodeException e) {
      return failedSchedulingOutput(label, new BTrace(), e);
    }
  }

  private Promise<Maybe<BValue>> scheduleJob(Job job) throws BytecodeException {
    return job.scheduleEvaluation(this);
  }

  Promise<Maybe<BValue>> doScheduleJob(Job job) throws BytecodeException {
    return switch (job.expr()) {
      case BCall call -> scheduleCall(job, call);
      case BCombine combine -> scheduleOperation(job, combine, CombineStep::new);
      case BIf if_ -> scheduleIf(job, if_);
      case BMap map -> scheduleMap(job, map);
      case BLambda lambda -> scheduleInlineTask(job);
      case BValue value -> promise(some(value));
      case BOrder order -> scheduleOperation(job, order, OrderStep::new);
      case BPick pick -> scheduleOperation(job, pick, PickStep::new);
      case BReference reference -> scheduleReference(job, reference);
      case BSelect select -> scheduleOperation(job, select, SelectStep::new);
      case BInvoke bInvoke -> scheduleOperation(job, bInvoke, InvokeStep::new);
    };
  }

  // Call operation

  private Promise<Maybe<BValue>> scheduleCall(Job callJob, BCall bCall) throws BytecodeException {
    var subExprs = bCall.subExprs();
    var lambda = subExprs.lambda();
    var lambdaArgs = subExprs.arguments();
    if (lambdaArgs instanceof BCombine combine) {
      return scheduleCallWithCombineArgs(callJob, bCall, lambda, combine);
    } else if (lambdaArgs instanceof BTuple tuple) {
      return scheduleCallWithTupleArgs(callJob, bCall, lambda, tuple);
    } else { // BExpr that evaluates to BTuple
      return scheduleCallWithExprArgs(callJob, bCall, lambda, lambdaArgs);
    }
  }

  private Promise<Maybe<BValue>> scheduleCallWithCombineArgs(
      Job callJob, BCall call, BExpr lambdaExpr, BCombine combine) throws BytecodeException {
    Task1<BValue, BValue> schedulingTask = (lambdaValue) -> {
      var bLambda = (BLambda) lambdaValue;
      var label = VM_LABEL.append("scheduleCall");
      try {
        var argJobs = combine.subExprs().items().map(e -> newJob(e, callJob));
        var bodyEnvironmentJobs = argJobs.appendAll(callJob.environment());
        var bodyTrace = bTrace(call.hash(), bLambda.hash(), callJob.trace());
        var bodyJob = newJob(bLambda.body(), bodyEnvironmentJobs, bodyTrace);
        return successOutput(scheduleJob(bodyJob), label, callJob.trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, callJob.trace(), e);
      }
    };
    return scheduler.submit(schedulingTask, scheduleNewJob(lambdaExpr, callJob));
  }

  private Promise<Maybe<BValue>> scheduleCallWithTupleArgs(
      Job callJob, BCall bCall, BExpr lambdaExpr, BTuple tuple) throws BytecodeException {
    Task1<BValue, BValue> schedulingTask = (lambdaValue) -> {
      var bLambda = (BLambda) lambdaValue;
      var label = VM_LABEL.append("scheduleCall");
      try {
        var result = scheduleCallBodyWithTupleArguments(callJob, bCall, lambdaExpr, tuple, bLambda);
        return successOutput(result, label, callJob.trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, callJob.trace(), e);
      }
    };
    var lambdaPromise = scheduleNewJob(lambdaExpr, callJob);
    return scheduler.submit(schedulingTask, lambdaPromise);
  }

  private Promise<Maybe<BValue>> scheduleCallWithExprArgs(
      Job callJob, BCall bCall, BExpr lambdaExpr, BExpr lambdaArgs) throws BytecodeException {
    Task2<BValue, BValue, BValue> schedulingTask = (lambdaValue, argsValue) -> {
      var label = VM_LABEL.append("scheduleCall");
      try {
        var bLambda = (BLambda) lambdaValue;
        var argsTuple = (BTuple) argsValue;
        return successOutput(
            scheduleCallBodyWithTupleArguments(callJob, bCall, lambdaExpr, argsTuple, bLambda),
            label,
            callJob.trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, callJob.trace(), e);
      }
    };
    /*
     * Performance can be improved. It just evaluates whole arguments expression
     * without taking into account whether lambda's body actually uses any argument at all.
     */
    var lambdaPromise = scheduleNewJob(lambdaExpr, callJob);
    var argsPromise = scheduleNewJob(lambdaArgs, callJob);
    return scheduler.submit(schedulingTask, lambdaPromise, argsPromise);
  }

  private Promise<Maybe<BValue>> scheduleCallBodyWithTupleArguments(
      Job callJob, BCall bCall, BExpr lambdaExpr, BTuple tuple, BLambda bLambda)
      throws BytecodeException {
    var argumentJobs = tuple.elements().map(this::newJob);
    var bodyEnvironmentJobs = argumentJobs.appendAll(callJob.environment());
    var bodyTrace = bTrace(bCall.hash(), lambdaExpr.hash(), callJob.trace());
    var bodyJob = newJob(bLambda.body(), bodyEnvironmentJobs, bodyTrace);
    return scheduleJob(bodyJob);
  }

  private Promise<Maybe<BValue>> scheduleIf(Job ifJob, BIf if_) throws BytecodeException {
    var subExprs = if_.subExprs();
    Task1<BValue, BValue> schedulingTask = (conditionValue) -> {
      var label = VM_LABEL.append("scheduleIf");
      try {
        var condition = ((BBool) conditionValue).toJavaBoolean();
        return successOutput(
            scheduleNewJob(condition ? subExprs.then_() : subExprs.else_(), ifJob),
            label,
            ifJob.trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, ifJob.trace(), e);
      }
    };
    var conditionPromise = scheduleNewJob(subExprs.condition(), ifJob);
    return scheduler.submit(schedulingTask, conditionPromise);
  }

  private Promise<Maybe<BValue>> scheduleMap(Job mapJob, BMap map) throws BytecodeException {
    var subExprs = map.subExprs();
    var arrayArg = subExprs.array();
    Task1<BValue, BValue> schedulingTask = (arrayValue) -> {
      var label = VM_LABEL.append("scheduleMap");
      try {
        var array = ((BArray) arrayValue);
        var mapperArg = subExprs.mapper();
        var calls = array.elements(BValue.class).map(e -> newCallB(mapperArg, e));
        var mappingLambdaResultType = ((BLambdaType) mapperArg.evaluationType()).result();
        var arrayType = bytecodeFactory.arrayType(mappingLambdaResultType);
        var order = bytecodeFactory.order(arrayType, calls);
        return successOutput(scheduleNewJob(order, mapJob), label, mapJob.trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, mapJob.trace(), e);
      }
    };
    var arrayPromise = scheduleNewJob(arrayArg, mapJob);
    return scheduler.submit(schedulingTask, arrayPromise);
  }

  private BExpr newCallB(BExpr lambdaExpr, BValue value) throws BytecodeException {
    return bytecodeFactory.call(lambdaExpr, singleArg(value));
  }

  private BExpr singleArg(BValue value) throws BytecodeException {
    return bytecodeFactory.tuple(list(value));
  }

  private <T extends BOperation> Promise<Maybe<BValue>> scheduleOperation(
      Job job, T operation, Function2<T, BTrace, Step, BytecodeException> stepFactory)
      throws BytecodeException {
    var step = stepFactory.apply(operation, job.trace());
    List<Job> subExprJobs = operation.subExprs().toList().map(e -> newJob(e, job));
    List<Promise<Maybe<BValue>>> subExprResults = subExprJobs.map(this::scheduleJob);
    return stepEvaluator.evaluate(step, subExprResults);
  }

  private Promise<Maybe<BValue>> scheduleReference(Job job, BReference reference)
      throws BytecodeException {
    int index = reference.index().toJavaBigInteger().intValue();
    var referencedJob = job.environment().get(index);
    var jobEvaluationType = referencedJob.expr().evaluationType();
    if (jobEvaluationType.equals(reference.evaluationType())) {
      return scheduleJob(referencedJob);
    } else {
      throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
          .formatted(index, jobEvaluationType.q(), reference.evaluationType().q()));
    }
  }

  private Promise<Maybe<BValue>> scheduleInlineTask(Job job) {
    Task0<BValue> inlineTask = () -> {
      var label = VM_LABEL.append("inline");
      try {
        var inlined = (BValue) bReferenceInliner.inline(job);
        return output(inlined, newReport(label, job.trace(), list()));
      } catch (BytecodeException e) {
        return failedOutput(label, job.trace(), "Vm inline Task failed with exception:", e);
      }
    };
    return scheduler.submit(inlineTask);
  }

  // helpers

  private Promise<Maybe<BValue>> scheduleNewJob(BExpr bExpr, Job parentJob)
      throws BytecodeException {
    return scheduleJob(newJob(bExpr, parentJob));
  }

  private static <T> Output<T> successOutput(
      Promise<Maybe<T>> resultPromise, Label label, BTrace trace) {
    return schedulingOutput(resultPromise, newReport(label, trace, list()));
  }

  private static <T> Output<T> failedSchedulingOutput(Label label, BTrace trace, Throwable e) {
    return failedOutput(label, trace, "Scheduling task failed with exception:", e);
  }

  private static <T> Output<T> failedOutput(
      Label label, BTrace trace, String message, Throwable e) {
    return output(null, newReport(label, trace, list(fatal(message, e))));
  }

  static Report newReport(Label label, BTrace trace, List<Log> logs) {
    return report(label, trace, logs);
  }

  private Job newJob(BExpr expr) {
    return newJob(expr, list(), new BTrace());
  }

  private Job newJob(BExpr expr, Job parentJob) {
    return newJob(expr, parentJob.environment(), parentJob.trace());
  }

  // Visible for testing
  protected Job newJob(BExpr expr, List<Job> environment, BTrace trace) {
    return new Job(expr, environment, trace);
  }
}
