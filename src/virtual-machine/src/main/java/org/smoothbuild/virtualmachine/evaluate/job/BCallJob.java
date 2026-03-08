package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.location.Locations.unknownLocation;
import static org.smoothbuild.common.schedule.Output.successOutput;
import static org.smoothbuild.virtualmachine.VmConstants.CALL_DEPTH_LIMIT;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.log.report.TraceLine;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateTuple;
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
    if (trace().depth() >= CALL_DEPTH_LIMIT) {
      throw new JobException("Call depth limit (%d) exceeded.".formatted(CALL_DEPTH_LIMIT));
    }
    var lambda = call.lambda();
    var lambdaArgs = call.arguments();
    if (lambdaArgs instanceof BCreateTuple createTuple) {
      return scheduleCallWithCreateTupleArgs(call, lambda, createTuple);
    } else if (lambdaArgs instanceof BTuple tuple) {
      return scheduleCallWithTupleArgs(call, lambda, tuple);
    } else { // BExpr that evaluates to BTuple
      return scheduleCallWithExprArgs(call, lambda, lambdaArgs);
    }
  }

  private Promise<Maybe<BValue>> scheduleCallWithCreateTupleArgs(
      BCall call, BExpr lambdaExpr, BCreateTuple createTuple) throws BytecodeException {
    var schedulingTask = newCallWithCreateTupleAsArgsSchedulingTask(call, createTuple);
    var lambdaPromise = evaluate(lambdaExpr);
    return scheduler().submit(schedulingTask, lambdaPromise);
  }

  private Task1<BValue, BValue> newCallWithCreateTupleAsArgsSchedulingTask(
      BCall call, BCreateTuple createTuple) {
    return (lambdaValue) -> {
      var bLambda = (BLambda) lambdaValue;
      try {
        var argJobs = createTuple.items().map(this::job);
        var bodyEnvironmentJobs = bodyEnvironmentJobs(bLambda, argJobs);
        var bodyTrace = newTrace(call, bLambda, trace());
        var schedule = job(bLambda.body(), bodyEnvironmentJobs, bodyTrace).evaluate();
        return successOutput(schedule, executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Promise<Maybe<BValue>> scheduleCallWithTupleArgs(
      BCall bCall, BExpr lambdaExpr, BTuple tuple) throws BytecodeException {
    var schedulingTask = newCallWithTupleArgsSchedulingTask(bCall, lambdaExpr, tuple);
    var lambdaPromise = evaluate(lambdaExpr);
    return scheduler().submit(schedulingTask, lambdaPromise);
  }

  private Task1<BValue, BValue> newCallWithTupleArgsSchedulingTask(
      BCall bCall, BExpr lambdaExpr, BTuple tuple) {
    return (lambdaValue) -> {
      var bLambda = (BLambda) lambdaValue;
      try {
        var result = scheduleCallBodyWithTupleArguments(
            tuple, bLambda, newTrace(bCall, lambdaExpr, trace()));
        return successOutput(result, executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Promise<Maybe<BValue>> scheduleCallWithExprArgs(
      BCall bCall, BExpr lambdaExpr, BExpr lambdaArgs) throws BytecodeException {
    var schedulingTask = newCallWithExprArgsSchedulingTask(bCall, lambdaExpr);
    /*
     * Performance can be improved. It just evaluates whole arguments expression
     * without taking into account whether lambda's body actually uses any argument at all.
     */
    var lambdaPromise = evaluate(lambdaExpr);
    var argsPromise = evaluate(lambdaArgs);
    return scheduler().submit(schedulingTask, lambdaPromise, argsPromise);
  }

  private Task2<BValue, BValue, BValue> newCallWithExprArgsSchedulingTask(
      BCall bCall, BExpr lambdaExpr) {
    return (lambdaValue, argsValue) -> {
      try {
        var bLambda = (BLambda) lambdaValue;
        var argsTuple = (BTuple) argsValue;
        var trace = newTrace(bCall, lambdaExpr, trace());
        return successOutput(
            scheduleCallBodyWithTupleArguments(argsTuple, bLambda, trace), executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Promise<Maybe<BValue>> scheduleCallBodyWithTupleArguments(
      BTuple tuple, BLambda bLambda, Trace trace) throws BytecodeException {
    var argumentJobs = tuple.elements().map(j -> job(j, list(), new Trace()));
    var bodyEnvironmentJobs = bodyEnvironmentJobs(bLambda, argumentJobs);
    var bodyJob = job(bLambda.body(), bodyEnvironmentJobs, trace);
    return bodyJob.evaluate();
  }

  private List<Job> bodyEnvironmentJobs(BLambda bLambda, List<Job> argumentJobs)
      throws BytecodeException {
    var lambdaJob = job(bLambda, list(), new Trace());
    return list(lambdaJob).addAll(argumentJobs).addAll(environment());
  }

  private Label executeLabel() {
    return scheduleLabel("execute");
  }

  private Trace newTrace(BCall call, BExpr called, Trace next) {
    var name = exprAttributes().names().getOrDefault(called.hash(), "???");
    var location = exprAttributes().locations().getOrDefault(call.hash(), unknownLocation());
    return new Trace(new TraceLine(name, location, next.topLine()));
  }
}
