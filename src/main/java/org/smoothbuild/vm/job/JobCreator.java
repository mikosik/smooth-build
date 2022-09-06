package org.smoothbuild.vm.job;

import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.job.TaskKind.CALL;
import static org.smoothbuild.vm.job.TaskKind.COMBINE;
import static org.smoothbuild.vm.job.TaskKind.INVOKE;
import static org.smoothbuild.vm.job.TaskKind.ORDER;
import static org.smoothbuild.vm.job.TaskKind.SELECT;

import java.util.Map;
import java.util.function.Function;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.IfB;
import org.smoothbuild.bytecode.expr.oper.InvokeB;
import org.smoothbuild.bytecode.expr.oper.MapB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.compile.lang.base.ExprInfoImpl;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.vm.algorithm.Algorithm;
import org.smoothbuild.vm.algorithm.CombineAlgorithm;
import org.smoothbuild.vm.algorithm.InvokeAlgorithm;
import org.smoothbuild.vm.algorithm.NativeMethodLoader;
import org.smoothbuild.vm.algorithm.OrderAlgorithm;
import org.smoothbuild.vm.algorithm.SelectAlgorithm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JobCreator {
  private static final String PARENTHESES = "()";
  private static final String PARENTHESES_INVOKE = "()~";
  private final NativeMethodLoader nativeMethodLoader;
  private final ImmutableMap<ExprB, ExprInfo> exprInfos;
  private final TaskCreator taskCreator;
  private final Map<Class<?>, Handler<?>> handler;
  private final ImmutableList<Job> bindings;

  public JobCreator(NativeMethodLoader nativeMethodLoader, BytecodeF bytecodeF,
      ImmutableMap<ExprB, ExprInfo> exprInfos) {
    this(nativeMethodLoader, bytecodeF, exprInfos, ImmutableList.of());
  }

  public JobCreator(NativeMethodLoader nativeMethodLoader, BytecodeF bytecodeF,
      ImmutableMap<ExprB, ExprInfo> exprInfos, ImmutableList<Job> bindings) {
    this(nativeMethodLoader, exprInfos, (a,  d, i) -> new Task(a, d, i, bytecodeF), bindings);
  }

  public JobCreator(NativeMethodLoader nativeMethodLoader,
      ImmutableMap<ExprB, ExprInfo> exprInfos, TaskCreator taskCreator) {
    this(nativeMethodLoader, exprInfos, taskCreator, ImmutableList.of());
  }

  public JobCreator(NativeMethodLoader nativeMethodLoader,
      ImmutableMap<ExprB, ExprInfo> exprInfos, TaskCreator taskCreator, ImmutableList<Job> bindings) {
    this.nativeMethodLoader = nativeMethodLoader;
    this.exprInfos = exprInfos;
    this.taskCreator = taskCreator;
    this.handler = createHandlers();
    this.bindings = bindings;
  }

  private ImmutableMap<Class<?>, Handler<?>> createHandlers() {
    return ImmutableMap.<Class<?>, Handler<?>>builder()
        .put(ArrayB.class, new Handler<>(this::constLazy, this::constEager))
        .put(BoolB.class, new Handler<>(this::constLazy, this::constEager))
        .put(BlobB.class, new Handler<>(this::constLazy, this::constEager))
        .put(CallB.class, new Handler<>(this::callLazy, this::callEager))
        .put(CombineB.class, new Handler<>(this::combineLazy, this::combineEager))
        .put(FuncB.class, new Handler<>(this::constLazy, this::constEager))
        .put(IfB.class, new Handler<>(this::ifLazy, this::ifEager))
        .put(IntB.class, new Handler<>(this::constLazy, this::constEager))
        .put(MapB.class, new Handler<>(this::mapLazy, this::mapEager))
        .put(InvokeB.class, new Handler<>(this::invokeLazy, this::invokeEager))
        .put(OrderB.class, new Handler<>(this::orderLazy, this::orderEager))
        .put(ParamRefB.class, new Handler<>(this::paramRefLazy, this::paramRefLazy))
        .put(SelectB.class, new Handler<>(this::selectLazy, this::selectEager))
        .put(StringB.class, new Handler<>(this::constLazy, this::constEager))
        .put(TupleB.class, new Handler<>(this::constLazy, this::constEager))
        .build();
  }

  private ImmutableList<Job> eagerJobsFor(ImmutableList<? extends ExprB> exprs) {
    return map(exprs, this::eagerJobFor);
  }

  private Job jobFor(ExprB expr, boolean eager) {
    return handlerFor(expr).job(eager).apply(expr);
  }

  public Job eagerJobFor(ExprB expr) {
    return handlerFor(expr).eagerJob().apply(expr);
  }

  private Job lazyJobFor(ExprB expr) {
    return handlerFor(expr).lazyJob().apply(expr);
  }

  private <T> Handler<T> handlerFor(ExprB expr) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) handler.get(expr.getClass());
    return result;
  }

  // Call

  private Job callLazy(CallB call) {
    return callJob(call, false);
  }

  private Job callEager(CallB call) {
    return callJob(call, true);
  }

  private Job callJob(CallB call, boolean eager) {
    var callData = call.data();
    var funcJ = jobFor(callData.callable(), eager);
    var argsJ = map(callData.args().items(), this::lazyJobFor);
    var loc = locFor(call);
    return callJob(funcJ, argsJ, loc, eager);
  }

  private Job callJob(Job func, ImmutableList<Job> args, Loc loc, boolean eager) {
    if (eager) {
      return callEagerJob(func, args, loc);
    } else {
      var resT = ((FuncTB) func.type()).res();
      return new LazyJob(resT, loc,
          () -> new CallJob(resT, func, args, loc, this));
    }
  }

  public Job callEagerJob(Job func, ImmutableList<Job> args, Loc loc) {
    var resT = ((FuncTB) func.type()).res();
    return new CallJob(resT, func, args, loc, this);
  }

  // Combine

  private Job combineLazy(CombineB combine) {
    var info = infoFor(combine);
    var loc = info.loc();
    return new LazyJob(combine.type(), loc, () -> combineEager(combine, info));
  }

  private Job combineEager(CombineB combine) {
    var info = infoFor(combine);
    return combineEager(combine, info);
  }

  private Job combineEager(CombineB combine, ExprInfo exprInfo) {
    var evalT = combine.type();
    var itemJs = eagerJobsFor(combine.items());
    var taskInfo = new TaskInfo(COMBINE, exprInfo);
    var algorithm = new CombineAlgorithm(evalT);
    return taskCreator.newTask(algorithm, itemJs, taskInfo);
  }

  // If

  private Job ifLazy(IfB if_) {
    var info = infoFor(if_);
    return new LazyJob(if_.type(), info.loc(), () -> ifEager(if_, info));
  }

  private Job ifEager(IfB if_) {
    var info = infoFor(if_);
    return ifEager(if_, info);
  }

  private Job ifEager(IfB if_, ExprInfo exprInfo) {
    var ifData = if_.data();
    var conditionJ = eagerJobFor(ifData.condition());
    var evalT = if_.type();
    var thenJ = lazyJobFor(ifData.then());
    var elseJ = lazyJobFor(ifData.else_());
    return new IfJob(evalT, conditionJ, thenJ, elseJ, exprInfo.loc());
  }

  // Invoke

  private Job invokeLazy(InvokeB invoke) {
    var info = infoFor(invoke);
    return new LazyJob(invoke.type(), info.loc(), () -> invokeEager(invoke, info));
  }

  private Job invokeEager(InvokeB invoke) {
    var info = infoFor(invoke);
    return invokeEager(invoke, info);
  }

  private Job invokeEager(InvokeB invoke, ExprInfo exprInfo) {
    var name = exprInfo.label();
    var invokeData = invoke.data();
    var methodT = invokeData.method().type();
    var argJs = eagerJobsFor(invokeData.args().items());
    var resT = methodT.res();
    var algorithm = new InvokeAlgorithm(resT, name, invokeData.method(), nativeMethodLoader);
    var taskInfo = new TaskInfo(INVOKE, name + PARENTHESES_INVOKE, exprInfo.loc());
    return taskCreator.newTask(algorithm, argJs, taskInfo);
  }

  // Map

  private Job mapLazy(MapB map) {
    var info = infoFor(map);
    return new LazyJob(map.type(), info.loc(), () -> mapEager(map, info));
  }

  private Job mapEager(MapB map) {
    var info = infoFor(map);
    return mapEager(map, info);
  }

  private Job mapEager(MapB mapB, ExprInfo exprInfo) {
    MapB.Data data = mapB.data();
    var arrayJ = eagerJobFor(data.array());
    var funcJ = eagerJobFor(data.func());
    var resT = mapB.type();
    return new MapJob(resT, arrayJ, funcJ, exprInfo.loc(), this);
  }

  // Order

  private Job orderLazy(OrderB order) {
    var info = infoFor(order);
    return new LazyJob(order.type(), info.loc(), () -> orderEager(order, info));
  }

  private Job orderEager(OrderB order) {
    var info = infoFor(order);
    return orderEager(order, info);
  }

  private Task orderEager(OrderB order, ExprInfo exprInfo) {
    var type = order.type();
    var elemJs = eagerJobsFor(order.elems());
    var taskInfo = new TaskInfo(ORDER, exprInfo);
    return orderEager(type, elemJs, taskInfo);
  }

  public Task orderEager(ArrayTB arrayTB, ImmutableList<Job> elemJs, TaskInfo info) {
    var algorithm = new OrderAlgorithm(arrayTB);
    return taskCreator.newTask(algorithm, elemJs, info);
  }

  // ParamRef

  private Job paramRefLazy(ParamRefB paramRef) {
    return bindings.get(paramRef.value().intValue());
  }

  // Select

  private Job selectLazy(SelectB select) {
    var info = infoFor(select);
    return new LazyJob(select.type(), info.loc(), () -> selectEager(select, info));
  }

  private Job selectEager(SelectB select) {
    var info = infoFor(select);
    return selectEager(select, info);
  }

  private Job selectEager(SelectB select, ExprInfo exprInfo) {
    var data = select.data();
    var selectableJ = eagerJobFor(data.selectable());
    var indexJ = eagerJobFor(data.index());
    var algorithmT = ((TupleTB) selectableJ.type()).items().get(data.index().toJ().intValue());
    var algorithm = new SelectAlgorithm(algorithmT);
    var taskInfo = new TaskInfo(SELECT, exprInfo);
    return taskCreator.newTask(algorithm, list(selectableJ, indexJ), taskInfo);
  }

  // Value

  private Job constLazy(ValB val) {
    var info = infoFor(val);
    var loc = info.loc();
    return new LazyJob(val.cat(), loc, () -> new ConstTask(val, info));
  }

  private Job constEager(ValB val) {
    var info = infoFor(val);
    return new ConstTask(val, info);
  }

  // helper methods

  public Job callFuncEagerJob(FuncB func, ImmutableList<Job> args, Loc loc) {
    var job = new JobCreator(nativeMethodLoader, exprInfos, taskCreator, concat(args, bindings))
        .eagerJobFor(func.body());
    var info = infoFor(func);
    var name = info.label() + PARENTHESES;
    return new VirtualTask(job, new TaskInfo(CALL, name, loc));
  }

  private ExprInfo infoFor(ExprB expr) {
    return requireNonNullElseGet(exprInfos.get(expr),
        () -> new ExprInfoImpl("@" + expr.hash(), Loc.unknown()));
  }

  private Loc locFor(ExprB expr) {
    var info = exprInfos.get(expr);
    if (info == null) {
      return Loc.unknown();
    } else {
      return info.loc();
    }
  }

  public record Handler<E>(Function<E, Job> lazyJob, Function<E, Job> eagerJob) {
    public Function<E, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }

  @FunctionalInterface
  public interface TaskCreator {
    public Task newTask(Algorithm algorithm, ImmutableList<Job> depJs, TaskInfo info);
  }
}
