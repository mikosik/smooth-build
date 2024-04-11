package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.virtualmachine.evaluate.execute.BTrace.bTrace;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function2;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf.BSubExprs;
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
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class BScheduler {
  private final TaskExecutor taskExecutor;
  private final BytecodeFactory bytecodeFactory;
  private final BReferenceInliner bReferenceInliner;

  @Inject
  public BScheduler(
      TaskExecutor taskExecutor,
      BytecodeFactory bytecodeFactory,
      BReferenceInliner bReferenceInliner) {
    this.taskExecutor = taskExecutor;
    this.bytecodeFactory = bytecodeFactory;
    this.bReferenceInliner = bReferenceInliner;
  }

  public void terminate() {
    taskExecutor.terminate();
  }

  public void awaitTermination() throws InterruptedException {
    taskExecutor.awaitTermination();
  }

  public Promise<BValue> scheduleExprEvaluation(BExpr expr) {
    var job = newJob(expr);
    scheduleJobEvaluation(job);
    return job.promisedValue();
  }

  private void scheduleJobEvaluation(Job job) {
    var wasAlreadyStarted = job.started().getAndSet(true);
    if (!wasAlreadyStarted) {
      taskExecutor.enqueue(() -> scheduleJobTasksEvaluation(job));
    }
  }

  private <T extends Throwable> void scheduleJobEvaluation(Job job, Consumer1<BValue, T> consumer) {
    scheduleJobEvaluation(job);
    job.promisedValue()
        .addConsumer((valueB) -> taskExecutor.enqueue(() -> consumer.accept(valueB)));
  }

  private void scheduleJobEvaluation(Job job, PromisedValue<BValue> consumer) {
    scheduleJobEvaluation(job);
    job.promisedValue().addConsumer(consumer);
  }

  private void scheduleJobTasksEvaluation(Job job) throws BytecodeException {
    switch (job.expr()) {
      case BCall call -> scheduleCall(job, call);
      case BCombine combine -> scheduleOperationTask(job, combine, CombineTask::new);
      case BIf if_ -> scheduleIfOperation(job, if_);
      case BMap map -> scheduleMapOperation(job, map);
      case BLambda lambda -> scheduleConstTask(job, (BValue) bReferenceInliner.inline(job));
      case BValue value -> scheduleConstTask(job, value);
      case BOrder order -> scheduleOperationTask(job, order, OrderTask::new);
      case BPick pick -> scheduleOperationTask(job, pick, PickTask::new);
      case BReference reference -> scheduleVarB(job, reference);
      case BSelect select -> scheduleOperationTask(job, select, SelectTask::new);
      case BInvoke bInvoke -> scheduleOperationTask(job, bInvoke, InvokeTask::newInvokeTask);
    }
  }

  // Call operation

  public void scheduleCall(Job callJob, BCall call) throws BytecodeException {
    var lambdaJob = newJob(call.subExprs().lambda(), callJob);
    scheduleJobEvaluation(
        lambdaJob, lambda -> handleCallWithEvaluatedLambda((BLambda) lambda, callJob, call));
  }

  private void handleCallWithEvaluatedLambda(BLambda lambda, Job callJob, BCall call)
      throws BytecodeException {
    BExpr argumentsExpr = call.subExprs().arguments();
    if (argumentsExpr instanceof BCombine combine) {
      handleCallWhenArgumentsIsCombineExpr(lambda, callJob, call, combine);
    } else if (argumentsExpr instanceof BTuple tuple) {
      handleCallWhenArgumentsIsTupleValue(lambda, callJob, call, tuple);
    } else {
      handleCallWhenArgumentsIsExpr(lambda, callJob, call, argumentsExpr);
    }
  }

  private void handleCallWhenArgumentsIsCombineExpr(
      BLambda lambda, Job callJob, BCall call, BCombine combine) throws BytecodeException {
    var arguments = combine.subExprs().items();
    var argumentJobs = arguments.map(e -> newJob(e, callJob));
    handleCallWithEvaluatedLambdaAndArgumentJobs(lambda, callJob, call, argumentJobs);
  }

  private void handleCallWhenArgumentsIsTupleValue(
      BLambda lambda, Job callJob, BCall call, BTuple tuple) throws BytecodeException {
    var argumentJobs = tuple.elements().map(this::newJob);
    handleCallWithEvaluatedLambdaAndArgumentJobs(lambda, callJob, call, argumentJobs);
  }

  /*
   * Performance of this schedule can be improved. Currently, it just evaluates whole expression
   * without taking into account whether function's body actually uses any argument at all.
   */
  private void handleCallWhenArgumentsIsExpr(
      BLambda lambda, Job callJob, BCall call, BExpr argumentsExpr) {
    scheduleJobEvaluation(
        newJob(argumentsExpr, callJob),
        arguments ->
            handleCallWhenArgumentsIsTupleValue(lambda, callJob, call, (BTuple) arguments));
  }

  private void handleCallWithEvaluatedLambdaAndArgumentJobs(
      BLambda lambda, Job callJob, BCall call, List<Job> argumentJobs) throws BytecodeException {
    var bodyEnvironmentJobs = argumentJobs.appendAll(callJob.environment());
    var bodyTrace = bTrace(call.hash(), lambda.hash(), callJob.trace());
    var bodyJob = newJob(lambda.body(), bodyEnvironmentJobs, bodyTrace);
    scheduleJobEvaluation(bodyJob, callJob.promisedValue());
  }

  // Const task

  private void scheduleConstTask(Job job, BValue value) {
    var constTask = new ConstTask(value, job.trace());
    scheduleJobTask(job, constTask, list());
  }

  // If operation

  private void scheduleIfOperation(Job ifJob, BIf if_) throws BytecodeException {
    var subExprs = if_.subExprs();
    var job = newJob(subExprs.condition(), ifJob);
    scheduleJobEvaluation(job, v -> onConditionEvaluated(v, ifJob, subExprs));
  }

  private void onConditionEvaluated(BValue condition, Job ifJob, BSubExprs args)
      throws BytecodeException {
    var expr = ((BBool) condition).toJavaBoolean() ? args.then_() : args.else_();
    scheduleJobEvaluation(newJob(expr, ifJob), ifJob.promisedValue());
  }

  // Map operation

  private void scheduleMapOperation(Job mapJob, BMap map) throws BytecodeException {
    var arrayArg = map.subExprs().array();
    var job = newJob(arrayArg, mapJob);
    scheduleJobEvaluation(
        job, v -> onMapArrayArgEvaluated((BArray) v, mapJob, map.subExprs().mapper()));
  }

  private void onMapArrayArgEvaluated(BArray array, Job mapJob, BExpr mapper)
      throws BytecodeException {
    var calls = array.elements(BValue.class).map(e -> newCallB(mapper, e));
    var mappingLambdaResultType = ((BLambdaType) mapper.evaluationType()).result();
    var order = bytecodeFactory.order(bytecodeFactory.arrayType(mappingLambdaResultType), calls);
    scheduleJobEvaluation(newJob(order, mapJob), mapJob.promisedValue());
  }

  private BExpr newCallB(BExpr lambdaExpr, BValue value) throws BytecodeException {
    return bytecodeFactory.call(lambdaExpr, singleArg(value));
  }

  private BExpr singleArg(BValue value) throws BytecodeException {
    return bytecodeFactory.tuple(list(value));
  }

  private <T extends BOperation> void scheduleOperationTask(
      Job job, T operation, Function2<T, BTrace, Task, BytecodeException> taskCreator)
      throws BytecodeException {
    var operationTask = taskCreator.apply(operation, job.trace());
    var subExprJobs = operation.subExprs().toList().map(e -> newJob(e, job));
    subExprJobs.forEach(this::scheduleJobEvaluation);
    scheduleJobTask(job, operationTask, subExprJobs);
  }

  private void scheduleVarB(Job job, BReference reference) throws BytecodeException {
    int index = reference.index().toJavaBigInteger().intValue();
    var referencedJob = job.environment().get(index);
    var jobEvaluationType = referencedJob.expr().evaluationType();
    if (jobEvaluationType.equals(reference.evaluationType())) {
      scheduleJobEvaluation(referencedJob, job.promisedValue());
    } else {
      throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
          .formatted(index, jobEvaluationType.q(), reference.evaluationType().q()));
    }
  }

  // helpers

  private void scheduleJobTask(Job job, Task task, List<Job> subExprJobs) {
    var subExprPromises = subExprJobs.map(Job::promisedValue);
    var consumer = job.promisedValue();
    runWhenAllAvailable(
        subExprPromises,
        () -> taskExecutor.enqueue(
            () -> taskExecutor.enqueue(task, toInput(subExprPromises), consumer)));
  }

  private BTuple toInput(List<? extends Promise<BValue>> depResults) throws BytecodeException {
    return bytecodeFactory.tuple(depResults.map(Promise::get));
  }

  private Job newJob(BExpr expr) {
    return newJob(expr, List.list(), new BTrace());
  }

  private Job newJob(BExpr expr, Job parentJob) {
    return newJob(expr, parentJob.environment(), parentJob.trace());
  }

  // Visible for testing
  protected Job newJob(BExpr expr, List<Job> environment, BTrace trace) {
    return new Job(expr, environment, trace);
  }
}
