package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BCallJob extends SchedulingJob {
  private final BCall call;

  public BCallJob(JobContext jobContext, BCall call, List<Job> environment, Trace trace) {
    super(jobContext, call, environment, trace);
    this.call = call;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws JobException, BytecodeException {
    var lambda = call.lambda();
    var lambdaArgs = call.arguments();
    if (lambdaArgs instanceof BConstructTuple constructTuple) {
      return scheduleCallWithConstructTupleArgs(lambda, constructTuple);
    } else if (lambdaArgs instanceof BTuple tuple) {
      return scheduleCallWithTupleArgs(lambda, tuple);
    } else { // BExpr that evaluates to BTuple
      return scheduleCallWithExprArgs(lambda, lambdaArgs);
    }
  }

  private Promise<Maybe<BValue>> scheduleCallWithConstructTupleArgs(
      BExpr lambdaExpr, BConstructTuple constructTuple) throws BytecodeException {
    var schedulingTask = newCallWithConstructTupleAsArgsSchedulingTask(constructTuple);
    var lambdaPromise = evaluate(lambdaExpr);
    return scheduler().submit(schedulingTask, lambdaPromise);
  }

  private Task1<BValue, BValue> newCallWithConstructTupleAsArgsSchedulingTask(
      BConstructTuple constructTuple) {
    return (lambdaValue) -> {
      var bLambda = (BLambda) lambdaValue;
      try {
        var argJobs = constructTuple.items().map(this::job);
        var bodyEnvironmentJobs = bodyEnvironmentJobs(bLambda, argJobs);
        var schedule = job(bLambda.body(), bodyEnvironmentJobs, trace()).evaluate();
        return successOutput(schedule, executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Promise<Maybe<BValue>> scheduleCallWithTupleArgs(BExpr lambdaExpr, BTuple tuple)
      throws BytecodeException {
    var schedulingTask = newCallWithTupleArgsSchedulingTask(tuple);
    var lambdaPromise = evaluate(lambdaExpr);
    return scheduler().submit(schedulingTask, lambdaPromise);
  }

  private Task1<BValue, BValue> newCallWithTupleArgsSchedulingTask(BTuple tuple) {
    return (lambdaValue) -> {
      var bLambda = (BLambda) lambdaValue;
      try {
        var result = scheduleCallBodyWithTupleArguments(tuple, bLambda, trace());
        return successOutput(result, executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Promise<Maybe<BValue>> scheduleCallWithExprArgs(BExpr lambdaExpr, BExpr lambdaArgs)
      throws BytecodeException {
    var schedulingTask = newCallWithExprArgsSchedulingTask();
    /*
     * Performance can be improved. It just evaluates whole arguments expression
     * without taking into account whether lambda's body actually uses any argument at all.
     */
    var lambdaPromise = evaluate(lambdaExpr);
    var argsPromise = evaluate(lambdaArgs);
    return scheduler().submit(schedulingTask, lambdaPromise, argsPromise);
  }

  private Task2<BValue, BValue, BValue> newCallWithExprArgsSchedulingTask() {
    return (lambdaValue, argsValue) -> {
      try {
        var bLambda = (BLambda) lambdaValue;
        var argsTuple = (BTuple) argsValue;
        return successOutput(
            scheduleCallBodyWithTupleArguments(argsTuple, bLambda, trace()),
            executeLabel(),
            trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Promise<Maybe<BValue>> scheduleCallBodyWithTupleArguments(
      BTuple tuple, BLambda bLambda, Trace trace) throws BytecodeException {
    var argumentJobs = tuple.elements().map(j -> job(j, list(), trace()));
    var bodyEnvironmentJobs = bodyEnvironmentJobs(bLambda, argumentJobs);
    var bodyJob = job(bLambda.body(), bodyEnvironmentJobs, trace);
    return bodyJob.evaluate();
  }

  private List<Job> bodyEnvironmentJobs(BLambda bLambda, List<Job> argumentJobs)
      throws BytecodeException {
    var lambdaJob = job(bLambda, list(), trace());
    return list(lambdaJob).addAll(argumentJobs);
  }

  private Label executeLabel() {
    return scheduleLabel("execute");
  }
}
