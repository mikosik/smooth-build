package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.BoolB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.expr.inst.FuncB;
import org.smoothbuild.bytecode.expr.inst.IfFuncB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.MapFuncB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.type.inst.FuncTB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TraceB;
import org.smoothbuild.vm.task.NativeCallTask;

import com.google.common.collect.ImmutableList;

public class CallJob extends Job {
  private final CallB callB;

  public CallJob(CallB callB, ExecutionContext context) {
    super(context);
    this.callB = callB;
  }

  @Override
  public Promise<InstB> evaluateImpl() {
    var result = new PromisedValue<InstB>();
    evaluateImpl(
        callB.dataSeq().get(0),
        funcB -> onFuncEvaluated(callB, funcB, result));
    return result;
  }

  private void onFuncEvaluated(CallB callB, InstB funcB, Consumer<InstB> resConsumer) {
    switch ((FuncB) funcB) {
      case DefFuncB defFuncB -> handleDefFunc(defFuncB, resConsumer);
      case IfFuncB ifFuncB -> handleIfFunc(resConsumer);
      case MapFuncB mapFuncB -> handleMapFunc(resConsumer);
      case NatFuncB natFuncB -> handleNatFunc(callB, natFuncB, resConsumer);
    }
  }

  // handling DefFunc

  private void handleDefFunc(DefFuncB defFuncB, Consumer<InstB> resultConsumer) {
    var argsJ = map(args(), context()::jobFor);
    var trace = trace(defFuncB);
    evaluateImpl(
        context().withEnvironment(argsJ, trace),
        defFuncB.body(),
        resultConsumer);
  }

  private TraceB trace(DefFuncB defFuncB) {
    return new TraceB(defFuncB.hash(), callB.hash(), context().trace());
  }

  // handling IfFunc

  private void handleIfFunc(Consumer<InstB> resultConsumer) {
    var args = args();
    evaluateImpl(
        args.get(0),
        v -> onConditionEvaluated(v, args, resultConsumer));
  }

  private void onConditionEvaluated(InstB conditionB, ImmutableList<ExprB> args,
      Consumer<InstB> resultConsumer) {
    evaluateImpl(
        args.get(((BoolB) conditionB).toJ() ? 1 : 2),
        resultConsumer);
  }

  // handling MapFunc

  private void handleMapFunc(Consumer<InstB> result) {
    evaluateImpl(
        args().get(0),
        a -> onMapArgsEvaluated((ArrayB) a, result));
  }

  private void onMapArgsEvaluated(ArrayB arrayB, Consumer<InstB> resultConsumer) {
    var mappingFuncExprB = args().get(1);
    var callBs = map(arrayB.elems(InstB.class), e -> newCallB(mappingFuncExprB, e));
    var mappingFuncResT = ((FuncTB) mappingFuncExprB.evalT()).res();
    var orderB = bytecodeF().order(bytecodeF().arrayT(mappingFuncResT), callBs);
    evaluateImpl(orderB, resultConsumer);
  }

  private ExprB newCallB(ExprB funcExprB, InstB val) {
    return bytecodeF().call(funcExprB, singleArg(val));
  }

  private CombineB singleArg(InstB val) {
    return bytecodeF().combine(list(val));
  }

  private BytecodeF bytecodeF() {
    return context().bytecodeF();
  }

  // handling NatFunc

  private void handleNatFunc(CallB callB, NatFuncB natFuncB, Consumer<InstB> res) {
    var task = new NativeCallTask(
        callB, natFuncB, context().nativeMethodLoader(), context().trace());
    evaluateTransitively(task, args())
        .addConsumer(res);
  }

  //helpers

  private void evaluateImpl(ExprB expr, Consumer<InstB> resultConsumer) {
    evaluateImpl(context(), expr, resultConsumer);
  }

  private void evaluateImpl(ExecutionContext context, ExprB expr, Consumer<InstB> resultConsumer) {
    context
        .jobFor(expr)
        .evaluate()
        .addConsumer(resultConsumer);
  }

  private ImmutableList<ExprB> args() {
    return ((CombineB) callB.dataSeq().get(1)).dataSeq();
  }
}
