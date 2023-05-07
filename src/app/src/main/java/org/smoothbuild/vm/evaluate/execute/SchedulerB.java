package org.smoothbuild.vm.evaluate.execute;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.ClosurizeB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OperB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.oper.VarB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.ClosureB;
import org.smoothbuild.vm.bytecode.expr.value.ExprFuncB;
import org.smoothbuild.vm.bytecode.expr.value.FuncB;
import org.smoothbuild.vm.bytecode.expr.value.IfFuncB;
import org.smoothbuild.vm.bytecode.expr.value.MapFuncB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.evaluate.task.CombineTask;
import org.smoothbuild.vm.evaluate.task.ConstTask;
import org.smoothbuild.vm.evaluate.task.InvokeTask;
import org.smoothbuild.vm.evaluate.task.OrderTask;
import org.smoothbuild.vm.evaluate.task.PickTask;
import org.smoothbuild.vm.evaluate.task.SelectTask;
import org.smoothbuild.vm.evaluate.task.Task;

import com.google.common.collect.ImmutableList;

public class SchedulerB {
  private final TaskExecutor taskExecutor;
  private final BytecodeF bytecodeF;
  private final VarReducerB varReducerB;

  @Inject
  public SchedulerB(
      TaskExecutor taskExecutor,
      BytecodeF bytecodeF,
      VarReducerB varReducerB) {
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

  private void scheduleJobEvaluationWithConsumer(Job job, Consumer<ValueB> consumer) {
    scheduleJobEvaluation(job);
    job.promisedValue().addConsumer(consumer);
  }

  private void scheduleJobTasksEvaluation(Job job) {
    // @formatter:off
    switch (job.exprB()) {
      case CallB      call      -> new CallScheduler(job, call).scheduleCall();
      case ClosurizeB closurize -> scheduleClosurize(job, closurize);
      case CombineB   combine   -> scheduleOperTask(job, combine, CombineTask::new);
      case ValueB     value     -> scheduleConstTask(job, value);
      case OrderB     order     -> scheduleOperTask(job, order, OrderTask::new);
      case PickB      pick      -> scheduleOperTask(job, pick, PickTask::new);
      case VarB       var       -> scheduleVarB(job, var);
      case SelectB    select    -> scheduleOperTask(job, select, SelectTask::new);
      // `default` is needed because ExprB is not sealed because it is in different package
      // than its subclasses and code is not modularized.
      default -> throw new RuntimeException("shouldn't happen");
    }
    // @formatter:on
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

    public void scheduleCall() {
      var exprB = call.subExprs().func();
      var funcJob = newJob(exprB, callJob);
      scheduleJobEvaluationWithConsumer(funcJob, this::onFuncEvaluated);
    }

    private void onFuncEvaluated(ValueB funcB) {
      switch ((FuncB) funcB) {
        // @formatter:off
        case ClosureB     closureB    -> handleClosure(closureB);
        case ExprFuncB    exprFuncB   -> handleExprFunc(exprFuncB);
        case IfFuncB      ifFuncB     -> handleIfFunc();
        case MapFuncB     mapFuncB    -> handleMapFunc();
        case NativeFuncB  nativeFuncB -> handleNativeFunc(nativeFuncB);
        // @formatter:on
      }
    }

    // functions with body

    private void handleClosure(ClosureB closureB) {
      var closureEnvJobs = map(closureB.environment().items(), SchedulerB.this::newJob);
      var bodyEnvironmentJobs = concat(argJobs(), closureEnvJobs);
      handleFunc(bodyEnvironmentJobs, closureB.func());
    }

    private void handleExprFunc(ExprFuncB exprFuncB) {
      var bodyEnvironmentJobs = concat(argJobs(), callJob.environment());
      handleFunc(bodyEnvironmentJobs, exprFuncB);
    }

    private void handleFunc(ImmutableList<Job> bodyEnvironment, ExprFuncB exprFuncB) {
      var bodyTrace = callTrace(exprFuncB);
      var bodyJob = newJob(exprFuncB.body(), bodyEnvironment, bodyTrace);
      scheduleJobEvaluationWithConsumer(bodyJob, callJob.promisedValue());
    }

    // handling IfFunc

    private void handleIfFunc() {
      var args = args();
      var job = newJob(args.get(0), callJob);
      scheduleJobEvaluationWithConsumer(job, v -> onConditionEvaluated(v, args));
    }

    private void onConditionEvaluated(ValueB conditionB, ImmutableList<ExprB> args) {
      var exprB = args.get(((BoolB) conditionB).toJ() ? 1 : 2);
      scheduleJobEvaluationWithConsumer(newJob(exprB, callJob), callJob.promisedValue());
    }

    // handling MapFunc

    private void handleMapFunc() {
      var arrayArg = args().get(0);
      var job = newJob(arrayArg, callJob);
      scheduleJobEvaluationWithConsumer(job, v -> onMapArrayArgEvaluated((ArrayB) v));
    }

    private void onMapArrayArgEvaluated(ArrayB arrayB) {
      var mappingFuncArg = args().get(1);
      var callBs = map(arrayB.elems(ValueB.class), e -> newCallB(mappingFuncArg, e));
      var mappingFuncResultT = ((FuncTB) mappingFuncArg.evaluationT()).result();
      var orderB = bytecodeF.order(bytecodeF.arrayT(mappingFuncResultT), callBs);
      scheduleJobEvaluationWithConsumer(newJob(orderB, callJob), callJob.promisedValue());
    }

    private ExprB newCallB(ExprB funcExprB, ValueB valueB) {
      return bytecodeF.call(funcExprB, singleArg(valueB));
    }

    private CombineB singleArg(ValueB valueB) {
      return bytecodeF.combine(list(valueB));
    }

    // handling NativeFunc

    private void handleNativeFunc(NativeFuncB nativeFuncB) {
      var trace = callTrace(nativeFuncB);
      var task = new InvokeTask(call, nativeFuncB, trace);
      var subExprJobs = argJobs();
      subExprJobs.forEach(SchedulerB.this::scheduleJobEvaluation);
      scheduleJobTask(callJob, task, subExprJobs);
    }

    // helpers

    private ImmutableList<Job> argJobs() {
      return map(args(), e -> newJob(e, callJob));
    }

    private TraceB callTrace(FuncB funcB) {
      return new TraceB(call.hash(), funcB.hash(), callJob.trace());
    }

    private ImmutableList<ExprB> args() {
      return call.subExprs().args().items();
    }
  }

  private void scheduleClosurize(Job job, ClosurizeB closurize) {
    var environment = varReducerB.inline(job.environment());
    var environmentB = bytecodeF.combine(environment);
    var closureB = bytecodeF.closure(environmentB, closurize.func());
    var closureJob = newJob(closureB, job);
    scheduleJobEvaluationWithConsumer(closureJob, job.promisedValue());
  }

  private void scheduleConstTask(Job job, ValueB value) {
    var constTask = new ConstTask(value, job.trace());
    scheduleJobTask(job, constTask, list());
  }

  private <T extends OperB> void scheduleOperTask(
      Job job, T operB, BiFunction<T, TraceB, Task> taskCreator) {
    var operTask = taskCreator.apply(operB, job.trace());
    var subExprJobs = map(operB.subExprs().toList(), e -> newJob(e, job));
    subExprJobs.forEach(this::scheduleJobEvaluation);
    scheduleJobTask(job, operTask, subExprJobs);
  }

  private void scheduleVarB(Job job, VarB varB) {
    int index = varB.index().toJ().intValue();
    var referencedJob = job.environment().get(index);
    var jobEvaluationT = referencedJob.exprB().evaluationT();
    if (jobEvaluationT.equals(varB.evaluationT())) {
      scheduleJobEvaluationWithConsumer(referencedJob, job.promisedValue());
    } else {
      throw new RuntimeException("environment(%d) evaluationT is %s but expected %s."
          .formatted(index, jobEvaluationT.q(), varB.evaluationT().q()));
    }
  }

  // helpers

  private void scheduleJobTask(Job job, Task task, ImmutableList<Job> subExprJobs) {
    var subExprPromises = map(subExprJobs, Job::promisedValue);
    var consumer = job.promisedValue();
    runWhenAllAvailable(
        subExprPromises, () -> taskExecutor.enqueue(task, toInput(subExprPromises), consumer));
  }

  private TupleB toInput(ImmutableList<? extends Promise<ValueB>> depResults) {
    return bytecodeF.tuple(map(depResults, Promise::get));
  }

  private Job newJob(ExprB exprB) {
    return newJob(exprB, list(), null);
  }

  private Job newJob(ExprB exprB, Job parentJob) {
    return newJob(exprB, parentJob.environment(), parentJob.trace());
  }

  // Visible for testing
  protected Job newJob(ExprB exprB, ImmutableList<Job> environment, TraceB trace) {
    return new Job(exprB, environment, trace);
  }
}
