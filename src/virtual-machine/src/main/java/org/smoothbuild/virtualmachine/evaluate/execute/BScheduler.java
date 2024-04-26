package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.virtualmachine.evaluate.execute.BTrace.bTrace;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.common.function.Function2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.common.schedule.TaskExecutor;
import org.smoothbuild.common.schedule.TaskX;
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
import org.smoothbuild.virtualmachine.evaluate.compute.ComputeException;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class BScheduler {
  public static final Label VM_SCHEDULER_LABEL = label("vm", "schedule");
  private final TaskExecutor taskExecutor;
  private final Computer computer;
  private final BytecodeFactory bytecodeFactory;
  private final BReferenceInliner bReferenceInliner;

  @Inject
  public BScheduler(
      TaskExecutor taskExecutor,
      Computer computer,
      BytecodeFactory bytecodeFactory,
      BReferenceInliner bReferenceInliner) {
    this.taskExecutor = taskExecutor;
    this.computer = computer;
    this.bytecodeFactory = bytecodeFactory;
    this.bReferenceInliner = bReferenceInliner;
  }

  public void awaitTermination() throws InterruptedException {
    taskExecutor.waitUntilIdle();
  }

  public Promise<BValue> scheduleExprEvaluation(BExpr expr) {
    return newJob(expr).evaluate();
  }

  private Promise<BValue> evaluate(Job job) {
    return scheduledTaskResult(taskExecutor.submit(() -> {
      try {
        return successOutput(job, scheduleJob(job));
      } catch (BytecodeException e) {
        return failedOutput(job, e);
      }
    }));
  }

  private Promise<BValue> scheduleJob(Job job) throws BytecodeException {
    return switch (job.expr()) {
      case BCall call -> scheduleCall(job, call);
      case BCombine combine -> scheduleOperation(job, combine, CombineTask::new);
      case BIf if_ -> scheduleIf(job, if_);
      case BMap map -> scheduleMap(job, map);
      case BLambda lambda -> scheduleConst(job, (BValue) bReferenceInliner.inline(job));
      case BValue value -> scheduleConst(job, value);
      case BOrder order -> scheduleOperation(job, order, OrderTask::new);
      case BPick pick -> scheduleOperation(job, pick, PickTask::new);
      case BReference reference -> scheduleReference(job, reference);
      case BSelect select -> scheduleOperation(job, select, SelectTask::new);
      case BInvoke bInvoke -> scheduleOperation(job, bInvoke, InvokeTask::newInvokeTask);
    };
  }

  // Call operation

  private Promise<BValue> scheduleCall(Job callJob, BCall bCall) throws BytecodeException {
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

  private PromisedValue<BValue> scheduleCallWithCombineArgs(
      Job callJob, BCall call, BExpr bLambda, BCombine combine) throws BytecodeException {
    Task1<Promise<BValue>, BValue> schedulingTask = (bValue) -> {
      try {
        var argJobs = combine.subExprs().items().map(e -> newJob(e, callJob));
        var bodyEnvironmentJobs = argJobs.appendAll(callJob.environment());
        var bodyTrace = bTrace(call.hash(), bValue.hash(), callJob.trace());
        var bodyJob = newJob(((BLambda) bValue).body(), bodyEnvironmentJobs, bodyTrace);
        return successOutput(callJob, scheduleJob(bodyJob));
      } catch (BytecodeException e) {
        return failedOutput(callJob, e);
      }
    };
    return scheduledTaskResult(
        taskExecutor.submit(schedulingTask, scheduleNewJob(bLambda, callJob)));
  }

  private Promise<BValue> scheduleCallWithTupleArgs(
      Job callJob, BCall bCall, BExpr lambdaExpr, BTuple tuple) throws BytecodeException {
    Task1<Promise<BValue>, BValue> schedulingTask = (lambdaValue) -> {
      try {
        var result = scheduleCallBodyWithTupleArguments(
            callJob, bCall, lambdaExpr, tuple, (BLambda) lambdaValue);
        return successOutput(callJob, result);
      } catch (BytecodeException e) {
        return failedOutput(callJob, e);
      }
    };
    var lambdaPromise = scheduleNewJob(lambdaExpr, callJob);
    return scheduledTaskResult(taskExecutor.submit(schedulingTask, lambdaPromise));
  }

  private Promise<BValue> scheduleCallWithExprArgs(
      Job callJob, BCall bCall, BExpr lambdaExpr, BExpr lambdaArgs) throws BytecodeException {
    Task2<Promise<BValue>, BValue, BValue> schedulingTask = (lambdaValue, argsValue) -> {
      try {
        var bLambda = (BLambda) lambdaValue;
        var argsTuple = (BTuple) argsValue;
        return successOutput(
            callJob,
            scheduleCallBodyWithTupleArguments(callJob, bCall, lambdaExpr, argsTuple, bLambda));
      } catch (BytecodeException e) {
        return failedOutput(callJob, e);
      }
    };
    /*
     * Performance can be improved. It just evaluates whole arguments expression
     * without taking into account whether lambda's body actually uses any argument at all.
     */
    var lambdaPromise = scheduleNewJob(lambdaExpr, callJob);
    var argsPromise = scheduleNewJob(lambdaArgs, callJob);
    return scheduledTaskResult(taskExecutor.submit(schedulingTask, lambdaPromise, argsPromise));
  }

  private Promise<BValue> scheduleCallBodyWithTupleArguments(
      Job callJob, BCall bCall, BExpr lambdaExpr, BTuple tuple, BLambda bLambda)
      throws BytecodeException {
    var argumentJobs = tuple.elements().map(j -> newJob(j));
    var bodyEnvironmentJobs = argumentJobs.appendAll(callJob.environment());
    var bodyTrace = bTrace(bCall.hash(), lambdaExpr.hash(), callJob.trace());
    var bodyJob = newJob(bLambda.body(), bodyEnvironmentJobs, bodyTrace);
    return scheduleJob(bodyJob);
  }

  private Promise<BValue> scheduleConst(Job job, BValue value) {
    var constTask = new ConstTask(value, job.trace());
    return submitVmTask(job, constTask, list());
  }

  private Promise<BValue> scheduleIf(Job ifJob, BIf if_) throws BytecodeException {
    var subExprs = if_.subExprs();
    Task1<Promise<BValue>, BValue> schedulingTask = (conditionValue) -> {
      try {
        var condition = ((BBool) conditionValue).toJavaBoolean();
        return successOutput(
            ifJob, scheduleNewJob(condition ? subExprs.then_() : subExprs.else_(), ifJob));
      } catch (BytecodeException e) {
        return failedOutput(ifJob, e);
      }
    };
    var conditionPromise = scheduleNewJob(subExprs.condition(), ifJob);
    return scheduledTaskResult(taskExecutor.submit(schedulingTask, conditionPromise));
  }

  private Promise<BValue> scheduleMap(Job mapJob, BMap map) throws BytecodeException {
    var subExprs = map.subExprs();
    var arrayArg = subExprs.array();
    Task1<Promise<BValue>, BValue> schedulingTask = (arrayValue) -> {
      try {
        var array = ((BArray) arrayValue);
        var mapperArg = subExprs.mapper();
        var calls = array.elements(BValue.class).map(e -> newCallB(mapperArg, e));
        var mappingLambdaResultType = ((BLambdaType) mapperArg.evaluationType()).result();
        var arrayType = bytecodeFactory.arrayType(mappingLambdaResultType);
        var order = bytecodeFactory.order(arrayType, calls);
        return successOutput(mapJob, scheduleNewJob(order, mapJob));
      } catch (BytecodeException e) {
        return failedOutput(mapJob, e);
      }
    };
    var arrayPromise = scheduleNewJob(arrayArg, mapJob);
    return scheduledTaskResult(taskExecutor.submit(schedulingTask, arrayPromise));
  }

  private BExpr newCallB(BExpr lambdaExpr, BValue value) throws BytecodeException {
    return bytecodeFactory.call(lambdaExpr, singleArg(value));
  }

  private BExpr singleArg(BValue value) throws BytecodeException {
    return bytecodeFactory.tuple(list(value));
  }

  private <T extends BOperation> Promise<BValue> scheduleOperation(
      Job job, T operation, Function2<T, BTrace, Task, BytecodeException> taskCreator)
      throws BytecodeException {
    var operationTask = taskCreator.apply(operation, job.trace());
    List<Job> subExprJobs = operation.subExprs().toList().map(e -> newJob(e, job));
    var subExprResults = subExprJobs.map(this::scheduleJob);
    return submitVmTask(job, operationTask, subExprResults);
  }

  private Promise<BValue> scheduleReference(Job job, BReference reference)
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

  // helpers

  private Promise<BValue> submitVmTask(Job job, Task task, List<Promise<BValue>> subExprResults) {
    TaskX<BValue, BValue> taskX = (bValues) -> {
      try {
        var result = computer.compute(task, toInput(subExprResults));
        var bValue = result.output().value();
        var report = TaskReportFactory.create(task, result);
        return output(bValue, report);
      } catch (ComputeException | BytecodeException | InterruptedException e) {
        return failedOutput(job, e, "Vm Task execution failed with exception:");
      }
    };
    return taskExecutor.submit(taskX, subExprResults);
  }

  private BTuple toInput(List<? extends Promise<BValue>> depResults) throws BytecodeException {
    return bytecodeFactory.tuple(depResults.map(Promise::get));
  }

  private Promise<BValue> scheduleNewJob(BExpr bExpr, Job parentJob) throws BytecodeException {
    return scheduleJob(newJob(bExpr, parentJob));
  }

  private static <T> Output<T> successOutput(Job job, T result) {
    return output(result, report(VM_SCHEDULER_LABEL, job.trace(), EXECUTION, list()));
  }

  private static <T> Output<T> failedOutput(Job job, Exception e) {
    return failedOutput(job, e, "Scheduling task failed with exception:");
  }

  private static <T> Output<T> failedOutput(Job job, Exception e, String message) {
    var logs = list(fatal(message, e));
    return output(null, report(VM_SCHEDULER_LABEL, job.trace(), EXECUTION, logs));
  }

  private static <T> PromisedValue<T> scheduledTaskResult(
      Promise<Promise<T>> schedulingTaskResult) {
    var result = new PromisedValue<T>();
    schedulingTaskResult.addConsumer(p -> p.addConsumer(result));
    return result;
  }

  public Job newJob(BExpr expr) {
    return newJob(expr, list(), new BTrace());
  }

  public Job newJob(BExpr expr, Job parentJob) {
    return newJob(expr, parentJob.environment(), parentJob.trace());
  }

  // Visible for testing
  protected Job newJob(BExpr expr, List<Job> environment, BTrace trace) {
    return new Job(expr, environment, trace, BScheduler.this::evaluate);
  }
}
