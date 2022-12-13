package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.value.ArrayB;
import org.smoothbuild.bytecode.expr.value.BoolB;
import org.smoothbuild.bytecode.expr.value.ClosureB;
import org.smoothbuild.bytecode.expr.value.ExprFuncB;
import org.smoothbuild.bytecode.expr.value.FuncB;
import org.smoothbuild.bytecode.expr.value.IfFuncB;
import org.smoothbuild.bytecode.expr.value.MapFuncB;
import org.smoothbuild.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.bytecode.type.value.FuncTB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TraceB;
import org.smoothbuild.vm.task.InvokeTask;

import com.google.common.collect.ImmutableList;

public class CallJob extends Job {
  public CallJob(CallB callB, ExecutionContext context) {
    super(callB, context);
  }

  @Override
  public CallB exprB() {
    return (CallB) super.exprB();
  }

  @Override
  public Promise<ValueB> evaluateImpl() {
    var result = new PromisedValue<ValueB>();
    evaluateImpl(
        exprB().dataSeq().get(0),
        funcB -> onFuncEvaluated(exprB(), funcB, result));
    return result;
  }

  private void onFuncEvaluated(CallB callB, ValueB funcB, Consumer<ValueB> resConsumer) {
    switch ((FuncB) funcB) {
      // @formatter:off
      case ClosureB     closureB     -> handleClosure(closureB, resConsumer);
      case ExprFuncB    exprFuncB    -> handleExprFunc(exprFuncB, resConsumer);
      case IfFuncB      ifFuncB      -> handleIfFunc(resConsumer);
      case MapFuncB     mapFuncB     -> handleMapFunc(resConsumer);
      case NativeFuncB  nativeFuncB  -> handleNativeFunc(callB, nativeFuncB, resConsumer);
      // @formatter:on
    }
  }

  // handling functions with body

  private void handleClosure(ClosureB closureB, Consumer<ValueB> resultConsumer) {
    var closureEnvironment = closureB.environment().items();
    var closureBodyEnvironment = map(concat(args(), closureEnvironment), context()::jobFor);
    handleFunc(closureBodyEnvironment, closureB.func(), resultConsumer);
  }

  private void handleExprFunc(ExprFuncB exprFuncB, Consumer<ValueB> resultConsumer) {
    var funcBodyEnvironment = map(args(), context()::jobFor);
    handleFunc(funcBodyEnvironment, exprFuncB, resultConsumer);
  }

  private void handleFunc(
      ImmutableList<Job> funcBodyEnvironment,
      ExprFuncB exprFuncB,
      Consumer<ValueB> resultConsumer) {
    var trace = trace(exprFuncB);
    var newContext = context().withEnvironment(funcBodyEnvironment, trace);
    evaluateImpl(newContext, exprFuncB.body(), resultConsumer);
  }

  // handling IfFunc

  private void handleIfFunc(Consumer<ValueB> resultConsumer) {
    var args = args();
    evaluateImpl(
        args.get(0),
        v -> onConditionEvaluated(v, args, resultConsumer));
  }

  private void onConditionEvaluated(ValueB conditionB, ImmutableList<ExprB> args,
      Consumer<ValueB> resultConsumer) {
    evaluateImpl(
        args.get(((BoolB) conditionB).toJ() ? 1 : 2),
        resultConsumer);
  }

  // handling MapFunc

  private void handleMapFunc(Consumer<ValueB> result) {
    evaluateImpl(
        args().get(0),
        a -> onMapArgsEvaluated((ArrayB) a, result));
  }

  private void onMapArgsEvaluated(ArrayB arrayB, Consumer<ValueB> resultConsumer) {
    var mappingFuncExprB = args().get(1);
    var callBs = map(arrayB.elems(ValueB.class), e -> newCallB(mappingFuncExprB, e));
    var mappingFuncResT = ((FuncTB) mappingFuncExprB.evalT()).result();
    var orderB = bytecodeF().order(bytecodeF().arrayT(mappingFuncResT), callBs);
    evaluateImpl(orderB, resultConsumer);
  }

  private ExprB newCallB(ExprB funcExprB, ValueB val) {
    return bytecodeF().call(funcExprB, singleArg(val));
  }

  private CombineB singleArg(ValueB val) {
    return bytecodeF().combine(list(val));
  }

  private BytecodeF bytecodeF() {
    return context().bytecodeF();
  }

  // handling NativeFunc

  private void handleNativeFunc(CallB callB, NativeFuncB nativeFuncB, Consumer<ValueB> res) {
    var trace = trace(nativeFuncB);
    var task = new InvokeTask(callB, nativeFuncB, context().nativeMethodLoader(), trace);
    evaluateTransitively(task, args())
        .addConsumer(res);
  }

  //helpers

  private void evaluateImpl(ExprB expr, Consumer<ValueB> resultConsumer) {
    evaluateImpl(context(), expr, resultConsumer);
  }

  private void evaluateImpl(ExecutionContext context, ExprB expr, Consumer<ValueB> resultConsumer) {
    context
        .jobFor(expr)
        .evaluate()
        .addConsumer(resultConsumer);
  }

  private ImmutableList<ExprB> args() {
    return ((CombineB) exprB().dataSeq().get(1)).dataSeq();
  }

  private TraceB trace(FuncB funcB) {
    return new TraceB(exprB().hash(), funcB.hash(), context().trace());
  }
}
