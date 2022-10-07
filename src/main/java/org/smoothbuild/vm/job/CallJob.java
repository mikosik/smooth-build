package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.execute.TaskKind.CALL;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.DefFuncB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.IfFuncB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.MapFuncB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TaskInfo;
import org.smoothbuild.vm.task.NativeCallTask;

import com.google.common.collect.ImmutableList;

public class CallJob extends ExecutingJob {
  private final CallB callB;

  public CallJob(CallB callB, ExecutionContext context) {
    super(context);
    this.callB = callB;
  }

  @Override
  public Promise<InstB> evaluateImpl() {
    var funcJ = context().jobFor(callB.data().callable());
    var result = new PromisedValue<InstB>();
    funcJ.evaluate()
        .addConsumer(instB -> onFuncJobCompleted(instB, result));
    return result;
  }

  private void onFuncJobCompleted(InstB instB, Consumer<InstB> resConsumer) {
    switch ((FuncB) instB) {
      case DefFuncB defFuncB -> handleDefFunc(defFuncB, resConsumer);
      case IfFuncB ifFuncB -> handleIfFunc(ifFuncB, resConsumer);
      case MapFuncB mapFuncB -> handleMapFunc(mapFuncB, resConsumer);
      case NatFuncB natFuncB -> handleNatFunc(natFuncB, resConsumer);
    }
  }

  // handling DefFunc

  private void handleDefFunc(DefFuncB defFuncB, Consumer<InstB> resultConsumer) {
    var argsJ = map(args(), context()::jobFor);
    var bodyJob = context()
        .withEnvironment(argsJ)
        .jobFor(defFuncB.body());
    var taskInfo = callTaskInfo(defFuncB);
    evaluateInsideVirtualJob(bodyJob, taskInfo, resultConsumer);
  }

  // handling IfFunc

  private void handleIfFunc(IfFuncB ifFuncB, Consumer<InstB> res) {
    var args = args();
    context()
        .jobFor(args.get(0))
        .evaluate()
        .addConsumer(v -> onConditionEvaluated(v, ifFuncB, args, res));
  }

  private void onConditionEvaluated(InstB conditionB, IfFuncB ifFuncB, ImmutableList<ExprB> args,
      Consumer<InstB> resultConsumer) {
    var condition = ((BoolB) conditionB).toJ();
    var job = context().jobFor(args.get(condition ? 1 : 2));
    var taskInfo = callTaskInfo(ifFuncB);
    evaluateInsideVirtualJob(job, taskInfo, resultConsumer);
  }

  // handling MapFunc

  private void handleMapFunc(MapFuncB mapFuncB, Consumer<InstB> result) {
    Promise<InstB> arrayJob = context().jobFor(args().get(0)).evaluate();
    arrayJob.addConsumer(a -> onMapDepsEvaluated((ArrayB) a, mapFuncB, result));
  }

  private void onMapDepsEvaluated(ArrayB arrayB,
      MapFuncB mapFuncB, Consumer<InstB> resultConsumer) {
    var mappingFuncExprB = args().get(1);
    var callBs = map(arrayB.elems(InstB.class), e -> newCallB(mappingFuncExprB, e));
    var mappingFuncResT = ((FuncTB) mappingFuncExprB.type()).res();
    var orderB = bytecodeF().order(bytecodeF().arrayT(mappingFuncResT), callBs);
    var orderJob = context().jobFor(orderB);
    var taskInfo = callTaskInfo(mapFuncB);
    evaluateInsideVirtualJob(orderJob, taskInfo, resultConsumer);
  }

  private ExprB newCallB(ExprB funcExprB, InstB val) {
    return bytecodeF().call(funcExprB, singleArg(val));
  }

  private CombineB singleArg(InstB val) {
    return bytecodeF().combine(bytecodeF().tupleT(val.type()), list(val));
  }

  private BytecodeF bytecodeF() {
    return context().bytecodeF();
  }

  // handling NatFunc

  private void handleNatFunc(NatFuncB natFuncB, Consumer<InstB> res) {
    var exprInfo = context().labeledLoc(natFuncB);
    var name = exprInfo.label();
    var resT = natFuncB.type().res();
    var task = new NativeCallTask(
        resT, name, natFuncB, context().nativeMethodLoader(), callTaskInfo(natFuncB));
    evaluateTransitively(task, args())
        .addConsumer(res);
  }

  //helpers

  private void evaluateInsideVirtualJob(Job job, TaskInfo taskInfo, Consumer<InstB> resultConsumer) {
    new VirtualJob(job, taskInfo, context().reporter())
        .evaluate()
        .addConsumer(resultConsumer);
  }

  private TaskInfo callTaskInfo(FuncB funcB) {
    var info = context().labeledLoc(funcB);
    return new TaskInfo(CALL, info.label() + "()", locFor(callB));
  }

  private Loc locFor(ExprB expr) {
    var info = context().labeledLoc(expr);
    if (info == null) {
      return Loc.unknown();
    } else {
      return info.loc();
    }
  }

  private ImmutableList<ExprB> args() {
    return callB.data().args().items();
  }
}
