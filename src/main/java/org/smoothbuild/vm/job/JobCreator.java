package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.job.TaskKind.CALL;
import static org.smoothbuild.vm.job.TaskKind.COMBINE;
import static org.smoothbuild.vm.job.TaskKind.INVOKE;
import static org.smoothbuild.vm.job.TaskKind.ORDER;
import static org.smoothbuild.vm.job.TaskKind.SELECT;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.bytecode.obj.cnst.BoolB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.FuncB;
import org.smoothbuild.bytecode.obj.cnst.IntB;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.base.NalImpl;
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
  private final ImmutableMap<ObjB, Nal> nals;
  private final TaskCreator taskCreator;
  private final Map<Class<?>, Handler<?>> handler;

  public JobCreator(NativeMethodLoader nativeMethodLoader, BytecodeF bytecodeF,
      ImmutableMap<ObjB, Nal> nals) {
    this(nativeMethodLoader, nals, (a,  d, i) -> new Task(a, d, i, bytecodeF));
  }

  public JobCreator(NativeMethodLoader nativeMethodLoader,
      ImmutableMap<ObjB, Nal> nals, TaskCreator taskCreator) {
    this.nativeMethodLoader = nativeMethodLoader;
    this.nals = nals;
    this.taskCreator = taskCreator;
    this.handler = createHandlers();
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

  public Job eagerJobFor(ObjB obj) {
    return eagerJobFor(obj, list());
  }

  private ImmutableList<Job> eagerJobsFor(ImmutableList<? extends ObjB> objs, List<Job> bindings) {
    return map(objs, e -> eagerJobFor(e, bindings));
  }

  private Job jobFor(ObjB expr, boolean eager, List<Job> bindings) {
    return handlerFor(expr).job(eager).apply(expr, bindings);
  }

  private Job eagerJobFor(ObjB expr, List<Job> bindings) {
    return handlerFor(expr).eagerJob().apply(expr, bindings);
  }

  private Job lazyJobFor(ObjB expr, List<Job> bindings) {
    return handlerFor(expr).lazyJob().apply(expr, bindings);
  }

  private <T> Handler<T> handlerFor(ObjB obj) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) handler.get(obj.getClass());
    return result;
  }

  // Call

  private Job callLazy(CallB call, List<Job> bindings) {
    return callJob(call, false, bindings);
  }

  private Job callEager(CallB call, List<Job> bindings) {
    return callJob(call, true, bindings);
  }

  private Job callJob(CallB call, boolean eager, List<Job> bindings) {
    var callData = call.data();
    var funcJ = jobFor(callData.callable(), eager, bindings);
    var argsJ = map(callData.args().items(), a -> lazyJobFor(a, bindings));
    var loc = locFor(call);
    return callJob(funcJ, argsJ, loc, eager, bindings);
  }

  private Job callJob(Job func, ImmutableList<Job> args, Loc loc, boolean eager, List<Job> bindings) {
    if (eager) {
      return callEagerJob(func, args, loc, bindings);
    } else {
      var resT = ((FuncTB) func.type()).res();
      return new LazyJob(resT, loc,
          () -> new CallJob(resT, func, args, loc, bindings, this));
    }
  }

  public Job callEagerJob(Job func, ImmutableList<Job> args, Loc loc, List<Job> bindings) {
    var resT = ((FuncTB) func.type()).res();
    return new CallJob(resT, func, args, loc, bindings, this);
  }

  // Combine

  private Job combineLazy(CombineB combine, List<Job> bindings) {
    var nal = nalFor(combine);
    var loc = nal.loc();
    return new LazyJob(combine.type(), loc, () -> combineEager(bindings, combine, nal));
  }

  private Job combineEager(CombineB combine, List<Job> bindings) {
    var nal = nalFor(combine);
    return combineEager(bindings, combine, nal);
  }

  private Job combineEager(List<Job> bindings, CombineB combine, Nal nal) {
    var evalT = combine.type();
    var itemJs = eagerJobsFor(combine.items(), bindings);
    var info = new TaskInfo(COMBINE, nal);
    var algorithm = new CombineAlgorithm(evalT);
    return taskCreator.newTask(algorithm, itemJs, info);
  }

  // If

  private Job ifLazy(IfB if_, List<Job> bindings) {
    var nal = nalFor(if_);
    return new LazyJob(if_.type(), nal.loc(), () -> ifEager(if_, nal, bindings));
  }

  private Job ifEager(IfB if_, List<Job> bindings) {
    var nal = nalFor(if_);
    return ifEager(if_, nal, bindings);
  }

  private Job ifEager(IfB if_, Nal nal, List<Job> bindings) {
    var ifData = if_.data();
    var conditionJ = eagerJobFor(ifData.condition(), bindings);
    var evalT = if_.type();
    var thenJ = lazyJobFor(ifData.then(), bindings);
    var elseJ = lazyJobFor(ifData.else_(), bindings);
    return new IfJob(evalT, conditionJ, thenJ, elseJ, nal.loc());
  }

  // Invoke

  private Job invokeLazy(InvokeB invoke, List<Job> bindings) {
    var nal = nalFor(invoke);
    return new LazyJob(invoke.type(), nal.loc(), () -> invokeEager(invoke, nal, bindings));
  }

  private Job invokeEager(InvokeB invoke, List<Job> bindings) {
    var nal = nalFor(invoke);
    return invokeEager(invoke, nal, bindings);
  }

  private Job invokeEager(InvokeB invoke, Nal nal, List<Job> bindings) {
    var name = nal.name();
    var invokeData = invoke.data();
    var methodT = invokeData.method().type();
    var argJs = eagerJobsFor(invokeData.args().items(), bindings);
    var resT = methodT.res();
    var algorithm = new InvokeAlgorithm(resT, name, invokeData.method(), nativeMethodLoader);
    var info = new TaskInfo(INVOKE, name + PARENTHESES_INVOKE, nal.loc());
    return taskCreator.newTask(algorithm, argJs, info);
  }

  // Map

  private Job mapLazy(MapB map, List<Job> bindings) {
    var nal = nalFor(map);
    return new LazyJob(map.type(), nal.loc(), () -> mapEager(map, nal, bindings));
  }

  private Job mapEager(MapB map, List<Job> bindings) {
    var nal = nalFor(map);
    return mapEager(map, nal, bindings);
  }

  private Job mapEager(MapB mapB, Nal nal, List<Job> bindings) {
    MapB.Data data = mapB.data();
    var arrayJ = eagerJobFor(data.array(), bindings);
    var funcJ = eagerJobFor(data.func(), bindings);
    var resT = mapB.type();
    return new MapJob(resT, arrayJ, funcJ, nal.loc(), bindings, this);
  }

  // Order

  private Job orderLazy(OrderB order, List<Job> bindings) {
    var nal = nalFor(order);
    return new LazyJob(order.type(), nal.loc(), () -> orderEager(order, nal, bindings));
  }

  private Job orderEager(OrderB order, List<Job> bindings) {
    var nal = nalFor(order);
    return orderEager(order, nal, bindings);
  }

  private Task orderEager(OrderB order, Nal nal, List<Job> bindings) {
    var type = order.type();
    var elemJs = map(order.elems(), e -> eagerJobFor(e, bindings));
    var info = new TaskInfo(ORDER, nal);
    return orderEager(type, elemJs, info);
  }

  public Task orderEager(ArrayTB arrayTB, ImmutableList<Job> elemJs, TaskInfo info) {
    var algorithm = new OrderAlgorithm(arrayTB);
    return taskCreator.newTask(algorithm, elemJs, info);
  }

  // ParamRef

  private Job paramRefLazy(ParamRefB paramRef, List<Job> bindings) {
    return bindings.get(paramRef.value().intValue());
  }

  // Select

  private Job selectLazy(SelectB select, List<Job> bindings) {
    var nal = nalFor(select);
    return new LazyJob(select.type(), nal.loc(), () -> selectEager(select, nal, bindings));
  }

  private Job selectEager(SelectB select, List<Job> bindings) {
    var nal = nalFor(select);
    return selectEager(select, nal, bindings);
  }

  private Job selectEager(
      SelectB select, Nal nal, List<Job> bindings) {
    var data = select.data();
    var selectableJ = eagerJobFor(data.selectable(), bindings);
    var indexJ = eagerJobFor(data.index(), bindings);
    var algorithmT = ((TupleTB) selectableJ.type()).items().get(data.index().toJ().intValue());
    var algorithm = new SelectAlgorithm(algorithmT);
    var info = new TaskInfo(SELECT, nal);
    return taskCreator.newTask(algorithm, list(selectableJ, indexJ), info);
  }

  // Value

  private Job constLazy(CnstB cnst, List<Job> bindings) {
    var nal = nalFor(cnst);
    var loc = nal.loc();
    return new LazyJob(cnst.cat(), loc, () -> new ConstTask(cnst, nal));
  }

  private Job constEager(CnstB cnst, List<Job> bindings) {
    var nal = nalFor(cnst);
    return new ConstTask(cnst, nal);
  }

  // helper methods

  public Job callFuncEagerJob(FuncB func, ImmutableList<Job> args, Loc loc, List<Job> bindings) {
    var job = eagerJobFor(func.body(), concat(args, bindings));
    var nal = nalFor(func);
    var name = nal.name() + PARENTHESES;
    return new VirtualTask(job, new TaskInfo(CALL, name, loc));
  }

  private Nal nalFor(ObjB obj) {
    Nal nal = nals.get(obj);
    if (nal == null) {
      return new NalImpl("@" + obj.hash(), Loc.unknown());
    } else {
      return nal;
    }
  }

  private Loc locFor(ObjB obj) {
    Nal nal = nals.get(obj);
    if (nal == null) {
      return Loc.unknown();
    } else {
      return nal.loc();
    }
  }

  public record Handler<E>(
      BiFunction<E, List<Job>, Job> lazyJob,
      BiFunction<E, List<Job>, Job> eagerJob) {
    public BiFunction<E, List<Job>, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }

  @FunctionalInterface
  public interface TaskCreator {
    public Task newTask(Algorithm algorithm, ImmutableList<Job> depJs, TaskInfo info);
  }
}
