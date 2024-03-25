package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;

import jakarta.inject.Inject;
import java.util.function.BiFunction;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf.SubExprsB;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOper;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
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
      case BCall call -> new CallScheduler(job, call).scheduleCall();
      case BCombine combine -> scheduleOperTask(job, combine, CombineTask::new);
      case BIf if_ -> scheduleIfFunc(job, if_);
      case BLambda lambda -> scheduleConstTask(job, (BValue) bReferenceInliner.inline(job));
      case BValue value -> scheduleConstTask(job, value);
      case BOrder order -> scheduleOperTask(job, order, OrderTask::new);
      case BPick pick -> scheduleOperTask(job, pick, PickTask::new);
      case BReference reference -> scheduleVarB(job, reference);
      case BSelect select -> scheduleOperTask(job, select, SelectTask::new);
        // `default` is needed because ExprB is not sealed because it is in different package
        // than its subclasses and code is not modularized.
      default -> throw new RuntimeException("shouldn't happen");
    }
  }

  /**
   * Helper class that stores job and call so they do not have to be passed to every method
   * that deals with call handling.
   */
  private class CallScheduler {
    private final Job callJob;
    private final BCall call;

    private CallScheduler(Job callJob, BCall call) {
      this.callJob = callJob;
      this.call = call;
    }

    public void scheduleCall() throws BytecodeException {
      var funcExpr = call.subExprs().func();
      var funcJob = newJob(funcExpr, callJob);
      scheduleJobEvaluation(funcJob, this::onFuncEvaluated);
    }

    private void onFuncEvaluated(BValue funcB) throws BytecodeException {
      switch ((BFunc) funcB) {
        case BLambda lambda -> handleLambda(lambda);
        case BMap map -> handleMapFunc();
        case BNativeFunc nativeFunc -> handleNativeFunc(nativeFunc);
      }
    }

    // functions with body

    private void handleLambda(BLambda lambda) throws BytecodeException {
      var bodyEnvironmentJobs = argJobs().appendAll(callJob.environment());
      var bodyTrace = callTrace(lambda);
      var bodyJob = newJob(lambda.body(), bodyEnvironmentJobs, bodyTrace);
      scheduleJobEvaluation(bodyJob, callJob.promisedValue());
    }

    // handling MapFunc

    private void handleMapFunc() throws BytecodeException {
      var arrayArg = args().get(0);
      var job = newJob(arrayArg, callJob);
      scheduleJobEvaluation(job, v -> onMapArrayArgEvaluated((BArray) v));
    }

    private void onMapArrayArgEvaluated(BArray array) throws BytecodeException {
      var mappingFuncArg = args().get(1);
      var calls = array.elements(BValue.class).map(e -> newCallB(mappingFuncArg, e));
      var mappingFuncResultType = ((BFuncType) mappingFuncArg.evaluationType()).result();
      var order = bytecodeFactory.order(bytecodeFactory.arrayType(mappingFuncResultType), calls);
      scheduleJobEvaluation(newJob(order, callJob), callJob.promisedValue());
    }

    private BExpr newCallB(BExpr funcExpr, BValue value) throws BytecodeException {
      return bytecodeFactory.call(funcExpr, singleArg(value));
    }

    private BCombine singleArg(BValue value) throws BytecodeException {
      return bytecodeFactory.combine(list(value));
    }

    // handling NativeFunc

    private void handleNativeFunc(BNativeFunc nativeFunc) throws BytecodeException {
      var trace = callTrace(nativeFunc);
      var task = new InvokeTask(call, nativeFunc, trace);
      var subExprJobs = argJobs();
      subExprJobs.forEach(BScheduler.this::scheduleJobEvaluation);
      scheduleJobTask(callJob, task, subExprJobs);
    }

    // helpers

    private List<Job> argJobs() throws BytecodeException {
      return args().map(e -> newJob(e, callJob));
    }

    private BTrace callTrace(BFunc func) {
      return new BTrace(call.hash(), func.hash(), callJob.trace());
    }

    private List<BExpr> args() throws BytecodeException {
      return call.subExprs().args().items();
    }
  }

  private void scheduleConstTask(Job job, BValue value) {
    var constTask = new ConstTask(value, job.trace());
    scheduleJobTask(job, constTask, list());
  }

  private void scheduleIfFunc(Job ifJob, BIf if_) throws BytecodeException {
    var args = if_.subExprs();
    var job = newJob(args.condition(), ifJob);
    scheduleJobEvaluation(job, v -> onConditionEvaluated(v, ifJob, args));
  }

  private void onConditionEvaluated(BValue condition, Job ifJob, SubExprsB args)
      throws BytecodeException {
    var expr = ((BBool) condition).toJavaBoolean() ? args.then_() : args.else_();
    scheduleJobEvaluation(newJob(expr, ifJob), ifJob.promisedValue());
  }

  private <T extends BOper> void scheduleOperTask(
      Job job, T operB, BiFunction<T, BTrace, Task> taskCreator) throws BytecodeException {
    var operTask = taskCreator.apply(operB, job.trace());
    var subExprJobs = operB.subExprs().toList().map(e -> newJob(e, job));
    subExprJobs.forEach(this::scheduleJobEvaluation);
    scheduleJobTask(job, operTask, subExprJobs);
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
