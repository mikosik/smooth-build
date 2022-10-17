package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.execute.TaskKind.CALL;

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
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.execute.TaskInfo;
import org.smoothbuild.vm.task.IdentityTask;
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
    var funcJ = context().jobFor(callB.dataSeq().get(0));
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
    var trace = trace(defFuncB);
    var bodyJob = context()
        .withEnvironment(argsJ, trace)
        .jobFor(defFuncB.body());
    var taskInfo = callTaskInfo(defFuncB);
    evaluateAndReportViaIdentityTask(bodyJob, taskInfo, resultConsumer);
  }

  private TraceS trace(DefFuncB defFuncB) {
    var tag = context().tagLoc(defFuncB).tag();
    var loc = context().tagLoc(callB).loc();
    return new TraceS(tag, loc, context().trace());
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
    evaluateAndReportViaIdentityTask(job, taskInfo, resultConsumer);
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
    var mappingFuncResT = ((FuncTB) mappingFuncExprB.evalT()).res();
    var orderB = bytecodeF().order(bytecodeF().arrayT(mappingFuncResT), callBs);
    var orderJob = context().jobFor(orderB);
    var taskInfo = callTaskInfo(mapFuncB);
    evaluateAndReportViaIdentityTask(orderJob, taskInfo, resultConsumer);
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

  private void handleNatFunc(NatFuncB natFuncB, Consumer<InstB> res) {
    var tagLoc = context().tagLoc(natFuncB);
    var tag = tagLoc.tag();
    var resT = natFuncB.evalT().res();
    var task = new NativeCallTask(resT, tag, natFuncB, context().nativeMethodLoader(),
        callTagLoc(natFuncB), context().trace());
    evaluateTransitively(task, args())
        .addConsumer(res);
  }

  //helpers

  private void evaluateAndReportViaIdentityTask(
      Job job, TaskInfo taskInfo, Consumer<InstB> resultConsumer) {
    job.evaluate().addConsumer(v -> onDependencyEvaluated(v, taskInfo, resultConsumer));
  }

  private void onDependencyEvaluated(
      InstB instB, TaskInfo taskInfo, Consumer<InstB> resultConsumer) {
    var task = new IdentityTask(instB.type(), CALL, taskInfo.tagLoc(), context().trace());
    var input = context().bytecodeF().tuple(list(instB));
    context().taskExecutor().enqueue(task, input, resultConsumer);
  }

  private TaskInfo callTaskInfo(FuncB funcB) {
    return new TaskInfo(CALL, callTagLoc(funcB), context().trace());
  }

  private TagLoc callTagLoc(FuncB funcB) {
    var tag = context().tagLoc(funcB).tag();
    return new TagLoc(tag + "()", locFor(callB));
  }

  private Loc locFor(ExprB expr) {
    var tagLoc = context().tagLoc(expr);
    if (tagLoc == null) {
      return Loc.unknown();
    } else {
      return tagLoc.loc();
    }
  }

  private ImmutableList<ExprB> args() {
    return ((CombineB) callB.dataSeq().get(1)).dataSeq();
  }
}
