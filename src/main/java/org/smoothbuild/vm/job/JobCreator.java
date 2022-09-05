package org.smoothbuild.vm.job;

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
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.base.NalImpl;
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
  private final ImmutableMap<ExprB, Nal> nals;
  private final TaskCreator taskCreator;
  private final Map<Class<?>, Handler<?>> handler;
  private final ImmutableList<Job> bindings;

  public JobCreator(NativeMethodLoader nativeMethodLoader, BytecodeF bytecodeF,
      ImmutableMap<ExprB, Nal> nals) {
    this(nativeMethodLoader, bytecodeF, nals, ImmutableList.of());
  }

  public JobCreator(NativeMethodLoader nativeMethodLoader, BytecodeF bytecodeF,
      ImmutableMap<ExprB, Nal> nals, ImmutableList<Job> bindings) {
    this(nativeMethodLoader, nals, (a,  d, i) -> new Task(a, d, i, bytecodeF), bindings);
  }

  public JobCreator(NativeMethodLoader nativeMethodLoader,
      ImmutableMap<ExprB, Nal> nals, TaskCreator taskCreator) {
    this(nativeMethodLoader, nals, taskCreator, ImmutableList.of());
  }

  public JobCreator(NativeMethodLoader nativeMethodLoader,
      ImmutableMap<ExprB, Nal> nals, TaskCreator taskCreator, ImmutableList<Job> bindings) {
    this.nativeMethodLoader = nativeMethodLoader;
    this.nals = nals;
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
    var nal = nalFor(combine);
    var loc = nal.loc();
    return new LazyJob(combine.type(), loc, () -> combineEager(combine, nal));
  }

  private Job combineEager(CombineB combine) {
    var nal = nalFor(combine);
    return combineEager(combine, nal);
  }

  private Job combineEager(CombineB combine, Nal nal) {
    var evalT = combine.type();
    var itemJs = eagerJobsFor(combine.items());
    var info = new TaskInfo(COMBINE, nal);
    var algorithm = new CombineAlgorithm(evalT);
    return taskCreator.newTask(algorithm, itemJs, info);
  }

  // If

  private Job ifLazy(IfB if_) {
    var nal = nalFor(if_);
    return new LazyJob(if_.type(), nal.loc(), () -> ifEager(if_, nal));
  }

  private Job ifEager(IfB if_) {
    var nal = nalFor(if_);
    return ifEager(if_, nal);
  }

  private Job ifEager(IfB if_, Nal nal) {
    var ifData = if_.data();
    var conditionJ = eagerJobFor(ifData.condition());
    var evalT = if_.type();
    var thenJ = lazyJobFor(ifData.then());
    var elseJ = lazyJobFor(ifData.else_());
    return new IfJob(evalT, conditionJ, thenJ, elseJ, nal.loc());
  }

  // Invoke

  private Job invokeLazy(InvokeB invoke) {
    var nal = nalFor(invoke);
    return new LazyJob(invoke.type(), nal.loc(), () -> invokeEager(invoke, nal));
  }

  private Job invokeEager(InvokeB invoke) {
    var nal = nalFor(invoke);
    return invokeEager(invoke, nal);
  }

  private Job invokeEager(InvokeB invoke, Nal nal) {
    var name = nal.name();
    var invokeData = invoke.data();
    var methodT = invokeData.method().type();
    var argJs = eagerJobsFor(invokeData.args().items());
    var resT = methodT.res();
    var algorithm = new InvokeAlgorithm(resT, name, invokeData.method(), nativeMethodLoader);
    var info = new TaskInfo(INVOKE, name + PARENTHESES_INVOKE, nal.loc());
    return taskCreator.newTask(algorithm, argJs, info);
  }

  // Map

  private Job mapLazy(MapB map) {
    var nal = nalFor(map);
    return new LazyJob(map.type(), nal.loc(), () -> mapEager(map, nal));
  }

  private Job mapEager(MapB map) {
    var nal = nalFor(map);
    return mapEager(map, nal);
  }

  private Job mapEager(MapB mapB, Nal nal) {
    MapB.Data data = mapB.data();
    var arrayJ = eagerJobFor(data.array());
    var funcJ = eagerJobFor(data.func());
    var resT = mapB.type();
    return new MapJob(resT, arrayJ, funcJ, nal.loc(), this);
  }

  // Order

  private Job orderLazy(OrderB order) {
    var nal = nalFor(order);
    return new LazyJob(order.type(), nal.loc(), () -> orderEager(order, nal));
  }

  private Job orderEager(OrderB order) {
    var nal = nalFor(order);
    return orderEager(order, nal);
  }

  private Task orderEager(OrderB order, Nal nal) {
    var type = order.type();
    var elemJs = eagerJobsFor(order.elems());
    var info = new TaskInfo(ORDER, nal);
    return orderEager(type, elemJs, info);
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
    var nal = nalFor(select);
    return new LazyJob(select.type(), nal.loc(), () -> selectEager(select, nal));
  }

  private Job selectEager(SelectB select) {
    var nal = nalFor(select);
    return selectEager(select, nal);
  }

  private Job selectEager(
      SelectB select, Nal nal) {
    var data = select.data();
    var selectableJ = eagerJobFor(data.selectable());
    var indexJ = eagerJobFor(data.index());
    var algorithmT = ((TupleTB) selectableJ.type()).items().get(data.index().toJ().intValue());
    var algorithm = new SelectAlgorithm(algorithmT);
    var info = new TaskInfo(SELECT, nal);
    return taskCreator.newTask(algorithm, list(selectableJ, indexJ), info);
  }

  // Value

  private Job constLazy(ValB val) {
    var nal = nalFor(val);
    var loc = nal.loc();
    return new LazyJob(val.cat(), loc, () -> new ConstTask(val, nal));
  }

  private Job constEager(ValB val) {
    var nal = nalFor(val);
    return new ConstTask(val, nal);
  }

  // helper methods

  public Job callFuncEagerJob(FuncB func, ImmutableList<Job> args, Loc loc) {
    var job = new JobCreator(nativeMethodLoader, nals, taskCreator, concat(args, bindings))
        .eagerJobFor(func.body());
    var nal = nalFor(func);
    var name = nal.name() + PARENTHESES;
    return new VirtualTask(job, new TaskInfo(CALL, name, loc));
  }

  private Nal nalFor(ExprB expr) {
    Nal nal = nals.get(expr);
    if (nal == null) {
      return new NalImpl("@" + expr.hash(), Loc.unknown());
    } else {
      return nal;
    }
  }

  private Loc locFor(ExprB expr) {
    Nal nal = nals.get(expr);
    if (nal == null) {
      return Loc.unknown();
    } else {
      return nal.loc();
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
