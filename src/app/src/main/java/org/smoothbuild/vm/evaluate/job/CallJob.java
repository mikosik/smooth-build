package org.smoothbuild.vm.evaluate.job;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.function.Consumer;

import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.ClosureB;
import org.smoothbuild.vm.bytecode.expr.value.ExprFuncB;
import org.smoothbuild.vm.bytecode.expr.value.FuncB;
import org.smoothbuild.vm.bytecode.expr.value.IfFuncB;
import org.smoothbuild.vm.bytecode.expr.value.MapFuncB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.evaluate.execute.TraceB;
import org.smoothbuild.vm.evaluate.task.InvokeTask;

import com.google.common.collect.ImmutableList;

public class CallJob extends Job {
  public CallJob(CallB callB, JobCreator jobCreator) {
    super(callB, jobCreator);
  }

  @Override
  public CallB exprB() {
    return (CallB) super.exprB();
  }

  @Override
  public void evaluateImpl(ExecutionContext context, Consumer<ValueB> result) {
    evaluateImpl(
        context,
        exprB().subExprs().func(),
        funcB -> onFuncEvaluated(context, exprB(), funcB, result));
  }

  private void onFuncEvaluated(ExecutionContext context, CallB callB, ValueB funcB,
      Consumer<ValueB> resConsumer) {
    switch ((FuncB) funcB) {
      // @formatter:off
      case ClosureB     closureB     -> handleClosure(context, closureB, resConsumer);
      case ExprFuncB    exprFuncB    -> handleExprFunc(context, exprFuncB, resConsumer);
      case IfFuncB      ifFuncB      -> handleIfFunc(context, resConsumer);
      case MapFuncB     mapFuncB     -> handleMapFunc(context, resConsumer);
      case NativeFuncB  nativeFuncB  -> handleNativeFunc(context, callB, nativeFuncB, resConsumer);
      // @formatter:on
    }
  }

  // handling functions with body

  private void handleClosure(ExecutionContext context, ClosureB closureB,
      Consumer<ValueB> resultConsumer) {
    var closureEnvironment = closureB.environment().items();
    var closureBodyEnvironment = map(concat(args(), closureEnvironment), jobCreator()::jobFor);
    handleFunc(context, closureBodyEnvironment, closureB.func(), resultConsumer);
  }

  private void handleExprFunc(ExecutionContext context, ExprFuncB exprFuncB,
      Consumer<ValueB> resultConsumer) {
    var funcBodyEnvironment = map(args(), jobCreator()::jobFor);
    handleFunc(context, funcBodyEnvironment, exprFuncB, resultConsumer);
  }

  private void handleFunc(
      ExecutionContext context,
      ImmutableList<Job> funcBodyEnvironment,
      ExprFuncB exprFuncB,
      Consumer<ValueB> resultConsumer) {
    var trace = trace(exprFuncB);
    var newJobCreator = jobCreator().withEnvironment(funcBodyEnvironment, trace);
    evaluateImpl(context, newJobCreator, exprFuncB.body(), resultConsumer);
  }

  // handling IfFunc

  private void handleIfFunc(ExecutionContext context, Consumer<ValueB> resultConsumer) {
    var args = args();
    evaluateImpl(
        context,
        args.get(0),
        v -> onConditionEvaluated(context, v, args, resultConsumer));
  }

  private void onConditionEvaluated(ExecutionContext context, ValueB conditionB,
      ImmutableList<ExprB> args, Consumer<ValueB> resultConsumer) {
    evaluateImpl(
        context,
        args.get(((BoolB) conditionB).toJ() ? 1 : 2),
        resultConsumer);
  }

  // handling MapFunc

  private void handleMapFunc(ExecutionContext context, Consumer<ValueB> result) {
    evaluateImpl(
        context,
        args().get(0),
        a -> onMapArgsEvaluated(context, (ArrayB) a, result));
  }

  private void onMapArgsEvaluated(
      ExecutionContext context, ArrayB arrayB, Consumer<ValueB> resultConsumer) {
    var mappingFuncExprB = args().get(1);
    var callBs = map(arrayB.elems(ValueB.class), e -> newCallB(context, mappingFuncExprB, e));
    var mappingFuncResultT = ((FuncTB) mappingFuncExprB.evaluationT()).result();
    var bytecodeF = context.bytecodeF();
    var orderB = bytecodeF.order(bytecodeF.arrayT(mappingFuncResultT), callBs);
    evaluateImpl(context, orderB, resultConsumer);
  }

  private ExprB newCallB(ExecutionContext context, ExprB funcExprB, ValueB val) {
    return context.bytecodeF().call(funcExprB, singleArg(context, val));
  }

  private CombineB singleArg(ExecutionContext context, ValueB val) {
    return context.bytecodeF().combine(list(val));
  }

  // handling NativeFunc

  private void handleNativeFunc(ExecutionContext context, CallB callB, NativeFuncB nativeFuncB,
      Consumer<ValueB> res) {
    var trace = trace(nativeFuncB);
    var task = new InvokeTask(callB, nativeFuncB, context.nativeMethodLoader(), trace);
    evaluateTransitively(context, task, args(), res);
  }

  //helpers

  private void evaluateImpl(ExecutionContext context, ExprB expr, Consumer<ValueB> resultConsumer) {
    evaluateImpl(context, jobCreator(), expr, resultConsumer);
  }

  private void evaluateImpl(ExecutionContext context, JobCreator jobCreator, ExprB expr,
      Consumer<ValueB> resultConsumer) {
    jobCreator
        .jobFor(expr)
        .evaluate(context)
        .addConsumer(resultConsumer);
  }

  private ImmutableList<ExprB> args() {
    return exprB().subExprs().args().items();
  }

  private TraceB trace(FuncB funcB) {
    return new TraceB(exprB().hash(), funcB.hash(), jobCreator().trace());
  }
}
