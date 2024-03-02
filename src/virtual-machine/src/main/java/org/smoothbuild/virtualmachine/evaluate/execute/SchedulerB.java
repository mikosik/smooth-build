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
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OperB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.VarB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.FuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IfFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.MapFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class SchedulerB {
  private final TaskExecutor taskExecutor;
  private final BytecodeF bytecodeF;
  private final VarReducerB varReducerB;

  @Inject
  public SchedulerB(TaskExecutor taskExecutor, BytecodeF bytecodeF, VarReducerB varReducerB) {
    this.taskExecutor = taskExecutor;
    this.bytecodeF = bytecodeF;
    this.varReducerB = varReducerB;
  }

  public void terminate() {
    taskExecutor.terminate();
  }

  public void awaitTermination() throws InterruptedException {
    taskExecutor.awaitTermination();
  }

  public Promise<ValueB> scheduleExprEvaluation(ExprB exprB) {
    var job = newJob(exprB);
    scheduleJobEvaluation(job);
    return job.promisedValue();
  }

  private void scheduleJobEvaluation(Job job) {
    var wasAlreadyStarted = job.started().getAndSet(true);
    if (!wasAlreadyStarted) {
      taskExecutor.enqueue(() -> scheduleJobTasksEvaluation(job));
    }
  }

  private <T extends Throwable> void scheduleJobEvaluation(Job job, Consumer1<ValueB, T> consumer) {
    scheduleJobEvaluation(job);
    job.promisedValue()
        .addConsumer((valueB) -> taskExecutor.enqueue(() -> consumer.accept(valueB)));
  }

  private void scheduleJobEvaluation(Job job, PromisedValue<ValueB> consumer) {
    scheduleJobEvaluation(job);
    job.promisedValue().addConsumer(consumer);
  }

  private void scheduleJobTasksEvaluation(Job job) throws BytecodeException {
    switch (job.exprB()) {
      case CallB call -> new CallScheduler(job, call).scheduleCall();
      case CombineB combine -> scheduleOperTask(job, combine, CombineTask::new);
      case LambdaB lambda -> scheduleConstTask(job, (ValueB) varReducerB.inline(job));
      case ValueB value -> scheduleConstTask(job, value);
      case OrderB order -> scheduleOperTask(job, order, OrderTask::new);
      case PickB pick -> scheduleOperTask(job, pick, PickTask::new);
      case VarB var -> scheduleVarB(job, var);
      case SelectB select -> scheduleOperTask(job, select, SelectTask::new);
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
    private final CallB call;

    private CallScheduler(Job callJob, CallB call) {
      this.callJob = callJob;
      this.call = call;
    }

    public void scheduleCall() throws BytecodeException {
      var exprB = call.subExprs().func();
      var funcJob = newJob(exprB, callJob);
      scheduleJobEvaluation(funcJob, this::onFuncEvaluated);
    }

    private void onFuncEvaluated(ValueB funcB) throws BytecodeException {
      switch ((FuncB) funcB) {
        case LambdaB lambdaB -> handleLambda(lambdaB);
        case IfFuncB ifFuncB -> handleIfFunc();
        case MapFuncB mapFuncB -> handleMapFunc();
        case NativeFuncB nativeFuncB -> handleNativeFunc(nativeFuncB);
      }
    }

    // functions with body

    private void handleLambda(LambdaB lambdaB) throws BytecodeException {
      var bodyEnvironmentJobs = argJobs().appendAll(callJob.environment());
      var bodyTrace = callTrace(lambdaB);
      var bodyJob = newJob(lambdaB.body(), bodyEnvironmentJobs, bodyTrace);
      scheduleJobEvaluation(bodyJob, callJob.promisedValue());
    }

    // handling IfFunc

    private void handleIfFunc() throws BytecodeException {
      var args = args();
      var job = newJob(args.get(0), callJob);
      scheduleJobEvaluation(job, v -> onConditionEvaluated(v, args));
    }

    private void onConditionEvaluated(ValueB conditionB, List<ExprB> args)
        throws BytecodeException {
      var exprB = args.get(((BoolB) conditionB).toJ() ? 1 : 2);
      scheduleJobEvaluation(newJob(exprB, callJob), callJob.promisedValue());
    }

    // handling MapFunc

    private void handleMapFunc() throws BytecodeException {
      var arrayArg = args().get(0);
      var job = newJob(arrayArg, callJob);
      scheduleJobEvaluation(job, v -> onMapArrayArgEvaluated((ArrayB) v));
    }

    private void onMapArrayArgEvaluated(ArrayB arrayB) throws BytecodeException {
      var mappingFuncArg = args().get(1);
      var callBs = arrayB.elements(ValueB.class).map(e -> newCallB(mappingFuncArg, e));
      var mappingFuncResultT = ((FuncTB) mappingFuncArg.evaluationType()).result();
      var orderB = bytecodeF.order(bytecodeF.arrayT(mappingFuncResultT), callBs);
      scheduleJobEvaluation(newJob(orderB, callJob), callJob.promisedValue());
    }

    private ExprB newCallB(ExprB funcExprB, ValueB valueB) throws BytecodeException {
      return bytecodeF.call(funcExprB, singleArg(valueB));
    }

    private CombineB singleArg(ValueB valueB) throws BytecodeException {
      return bytecodeF.combine(list(valueB));
    }

    // handling NativeFunc

    private void handleNativeFunc(NativeFuncB nativeFuncB) throws BytecodeException {
      var trace = callTrace(nativeFuncB);
      var task = new InvokeTask(call, nativeFuncB, trace);
      var subExprJobs = argJobs();
      subExprJobs.forEach(SchedulerB.this::scheduleJobEvaluation);
      scheduleJobTask(callJob, task, subExprJobs);
    }

    // helpers

    private List<Job> argJobs() throws BytecodeException {
      return args().map(e -> newJob(e, callJob));
    }

    private TraceB callTrace(FuncB funcB) {
      return new TraceB(call.hash(), funcB.hash(), callJob.trace());
    }

    private List<ExprB> args() throws BytecodeException {
      return call.subExprs().args().items();
    }
  }

  private void scheduleConstTask(Job job, ValueB value) {
    var constTask = new ConstTask(value, job.trace());
    scheduleJobTask(job, constTask, list());
  }

  private <T extends OperB> void scheduleOperTask(
      Job job, T operB, BiFunction<T, TraceB, Task> taskCreator) throws BytecodeException {
    var operTask = taskCreator.apply(operB, job.trace());
    var subExprJobs = operB.subExprs().toList().map(e -> newJob(e, job));
    subExprJobs.forEach(this::scheduleJobEvaluation);
    scheduleJobTask(job, operTask, subExprJobs);
  }

  private void scheduleVarB(Job job, VarB varB) throws BytecodeException {
    int index = varB.index().toJ().intValue();
    var referencedJob = job.environment().get(index);
    var jobEvaluationType = referencedJob.exprB().evaluationType();
    if (jobEvaluationType.equals(varB.evaluationType())) {
      scheduleJobEvaluation(referencedJob, job.promisedValue());
    } else {
      throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
          .formatted(index, jobEvaluationType.q(), varB.evaluationType().q()));
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

  private TupleB toInput(List<? extends Promise<ValueB>> depResults) throws BytecodeException {
    return bytecodeF.tuple(depResults.map(Promise::get));
  }

  private Job newJob(ExprB exprB) {
    return newJob(exprB, List.list(), new TraceB());
  }

  private Job newJob(ExprB exprB, Job parentJob) {
    return newJob(exprB, parentJob.environment(), parentJob.trace());
  }

  // Visible for testing
  protected Job newJob(ExprB exprB, List<Job> environment, TraceB trace) {
    return new Job(exprB, environment, trace);
  }
}
