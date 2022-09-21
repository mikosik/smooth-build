package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;
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
import org.smoothbuild.bytecode.expr.val.MapFuncB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.ValB;
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
  public Promise<ValB> evaluateImpl() {
    var funcJ = context().jobFor(callB.data().callable());
    var result = new PromisedValue<ValB>();
    funcJ.evaluate()
        .addConsumer(valB -> onFuncJobCompleted(valB, result));
    return result;
  }

  private void onFuncJobCompleted(ValB valB, Consumer<ValB> resConsumer) {
    switch ((FuncB) valB) {
      case DefFuncB defFuncB -> handleDefFunc(defFuncB, resConsumer);
      case IfFuncB ifFuncB -> handleIfFunc(ifFuncB, resConsumer);
      case MapFuncB mapFuncB -> handleMapFunc(mapFuncB, resConsumer);
      case NatFuncB natFuncB -> handleNatFunc(natFuncB, resConsumer);
    }
  }

  // handling DefFunc

  private void handleDefFunc(DefFuncB defFuncB, Consumer<ValB> resultConsumer) {
    var argsJ = map(args(), context()::jobFor);
    var bodyJob = context()
        .withBindings(argsJ)
        .jobFor(defFuncB.body());
    var taskInfo = callTaskInfo(defFuncB);
    evaluateInsideVirtualJob(bodyJob, taskInfo, resultConsumer);
  }

  // handling if function

  private void handleIfFunc(IfFuncB ifFuncB, Consumer<ValB> res) {
    var args = args();
    context()
        .jobFor(args.get(0))
        .evaluate()
        .addConsumer(v -> onConditionEvaluated(v, ifFuncB, args, res));
  }

  private void onConditionEvaluated(ValB conditionValB, IfFuncB ifFuncB, ImmutableList<ExprB> args,
      Consumer<ValB> resultConsumer) {
    var condition = ((BoolB) conditionValB).toJ();
    var job = context().jobFor(args.get(condition ? 1 : 2));
    var taskInfo = callTaskInfo(ifFuncB);
    evaluateInsideVirtualJob(job, taskInfo, resultConsumer);
  }

  // handling map function

  private void handleMapFunc(MapFuncB mapFuncB, Consumer<ValB> result) {
    var args = args();
    var argJobs = map(args, context()::jobFor);
    var argResults = map(argJobs, Job::evaluate);
    runWhenAllAvailable(argResults, () -> onMapDepsEvaluated(argResults, mapFuncB, result));
  }

  private void onMapDepsEvaluated(ImmutableList<Promise<ValB>> argResults,
      MapFuncB mapFuncB, Consumer<ValB> resultConsumer) {
    var arrayB = (ArrayB) argResults.get(0).get();
    var funcB = (FuncB) argResults.get(1).get();
    var callBs = map(arrayB.elems(ValB.class), e -> newCallB(funcB, e));
    var orderB = bytecodeF().order(bytecodeF().arrayT(funcB.type().res()), callBs);
    var orderJob = context().jobFor(orderB);
    var taskInfo = callTaskInfo(mapFuncB);
    evaluateInsideVirtualJob(orderJob, taskInfo, resultConsumer);
  }

  private ExprB newCallB(FuncB funcB, ValB val) {
    return bytecodeF().call(funcB.type().res(), funcB, singleArg(val));
  }

  private CombineB singleArg(ValB val) {
    return bytecodeF().combine(bytecodeF().tupleT(val.type()), list(val));
  }

  private BytecodeF bytecodeF() {
    return context().bytecodeF();
  }

  // handling NatFunc

  private void handleNatFunc(NatFuncB natFuncB, Consumer<ValB> res) {
    var exprInfo = context().labeledLoc(natFuncB);
    var name = exprInfo.label();
    var resT = natFuncB.type().res();
    var task = new NativeCallTask(
        resT, name, natFuncB, context().nativeMethodLoader(), callTaskInfo(natFuncB));
    evaluateTransitively(task, args())
        .addConsumer(res);
  }

  //helpers

  private void evaluateInsideVirtualJob(Job job, TaskInfo taskInfo, Consumer<ValB> resultConsumer) {
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
