package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.vm.job.TaskKind.CALL;
import static org.smoothbuild.vm.job.TaskKind.COMBINE;
import static org.smoothbuild.vm.job.TaskKind.CONVERT;
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
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.base.NalImpl;
import org.smoothbuild.vm.algorithm.Algorithm;
import org.smoothbuild.vm.algorithm.CombineAlgorithm;
import org.smoothbuild.vm.algorithm.ConvertAlgorithm;
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
    return eagerJobFor(list(), obj);
  }

  private ImmutableList<Job> eagerJobsFor(ImmutableList<? extends ObjB> objs, List<Job> scope) {
    return map(objs, e -> eagerJobFor(scope, e));
  }

  private Job jobFor(List<Job> scope, ObjB expr, boolean eager) {
    return handlerFor(expr).job(eager).apply(expr, scope);
  }

  private Job eagerJobFor(List<Job> scope, ObjB expr) {
    return handlerFor(expr).eagerJob().apply(expr, scope);
  }

  private Job lazyJobFor(List<Job> scope, ObjB expr) {
    return handlerFor(expr).lazyJob().apply(expr, scope);
  }

  private <T> Handler<T> handlerFor(ObjB obj) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) handler.get(obj.getClass());
    return result;
  }

  // Call

  private Job callLazy(CallB call, List<Job> scope) {
    return callJob(call, false, scope);
  }

  private Job callEager(CallB call, List<Job> scope) {
    return callJob(call, true, scope);
  }

  private Job callJob(CallB call, boolean eager, List<Job> scope) {
    var callData = call.data();
    var funcJ = jobFor(scope, callData.callable(), eager);
    var argsJ = map(callData.args().items(), a -> lazyJobFor(scope, a));
    var loc = locFor(call);
    var actualEvalT = call.type();
    return callJob(actualEvalT, funcJ, argsJ, loc, eager, scope);
  }

  private Job callJob(TypeB actualEvalT, Job func, ImmutableList<Job> args, Loc loc, boolean eager,
      List<Job> scope) {
    if (eager) {
      return callEagerJob(actualEvalT, func, args, loc, scope);
    } else {
      var funcT = (FuncTB) func.type();
      var resT = funcT.res();
      return new LazyJob(actualEvalT, loc,
          () -> callEagerJob(actualEvalT, resT, func, args, loc, scope));
    }
  }

  public Job callEagerJob(TypeB actualEvalT, Job func, ImmutableList<Job> args, Loc loc,
      List<Job> scope) {
    var funcT = (FuncTB) func.type();
    var resT = funcT.res();
    return callEagerJob(actualEvalT, resT, func, args, loc, scope);
  }

  private Job callEagerJob(TypeB actualEvalT, TypeB actualResT, Job func, ImmutableList<Job> args,
      Loc loc, List<Job> scope) {
    var callJ = new CallJob(actualResT, func, args, loc, scope, JobCreator.this);
    return convertIfNeeded(actualEvalT, loc, callJ);
  }

  // Combine

  private Job combineLazy(CombineB combine, List<Job> scope) {
    var nal = nalFor(combine);
    var loc = nal.loc();
    return new LazyJob(combine.type(), loc, () -> combineEager(scope, combine, nal));
  }

  private Job combineEager(CombineB combine, List<Job> scope) {
    var nal = nalFor(combine);
    return combineEager(scope, combine, nal);
  }

  private Job combineEager(List<Job> scope, CombineB combine, Nal nal) {
    var evalT = combine.type();
    var itemJs = eagerJobsFor(combine.items(), scope);
    var convertedItemJs = convertJobs(evalT.items(), nal, itemJs);
    var info = new TaskInfo(COMBINE, nal);
    var algorithm = new CombineAlgorithm(evalT);
    return taskCreator.newTask(algorithm, convertedItemJs, info);
  }

  // If

  private Job ifLazy(IfB if_, List<Job> scope) {
    var nal = nalFor(if_);
    return new LazyJob(if_.type(), nal.loc(), () -> ifEager(if_, nal, scope));
  }

  private Job ifEager(IfB if_, List<Job> scope) {
    var nal = nalFor(if_);
    return ifEager(if_, nal, scope);
  }

  private Job ifEager(IfB if_, Nal nal, List<Job> scope) {
    var ifData = if_.data();
    var conditionJ = eagerJobFor(scope, ifData.condition());
    var evalT = if_.type();
    var thenJ = clauseJob(evalT, nal, ifData.then(), scope);
    var elseJ = clauseJob(evalT, nal, ifData.else_(), scope);
    return new IfJob(evalT, conditionJ, thenJ, elseJ, nal.loc());
  }

  private Job clauseJob(TypeB actualEvalT, Nal nal, ObjB clause, List<Job> scope) {
    return convertIfNeeded(actualEvalT, nal.loc(), lazyJobFor(scope, clause));
  }

  // Invoke

  private Job invokeLazy(InvokeB invoke, List<Job> scope) {
    var nal = nalFor(invoke);
    return new LazyJob(invoke.type(), nal.loc(), () -> invokeEager(invoke, nal, scope));
  }

  private Job invokeEager(InvokeB invoke, List<Job> scope) {
    var nal = nalFor(invoke);
    return invokeEager(invoke, nal, scope);
  }

  private Job invokeEager(InvokeB invoke, Nal nal, List<Job> scope) {
    var name = nal.name();
    var invokeData = invoke.data();
    var methodT = invokeData.method().type();
    var argJs = eagerJobsFor(invokeData.args().items(), scope);
    var actualResT = methodT.res();
    var algorithm = new InvokeAlgorithm(actualResT, name, invokeData.method(), nativeMethodLoader);
    var info = new TaskInfo(INVOKE, name + PARENTHESES_INVOKE, nal.loc());
    var convertedArgJs = convertJobs(methodT.params(), nal, argJs);
    var task = taskCreator.newTask(algorithm, convertedArgJs, info);
    var type = invoke.type();
    return convertIfNeeded(type, nal.loc(), task);
  }

  // Map

  private Job mapLazy(MapB map, List<Job> scope) {
    var nal = nalFor(map);
    return new LazyJob(map.type(), nal.loc(), () -> mapEager(map, nal, scope));
  }

  private Job mapEager(MapB map, List<Job> scope) {
    var nal = nalFor(map);
    return mapEager(map, nal, scope);
  }

  private Job mapEager(MapB mapB, Nal nal, List<Job> scope) {
    MapB.Data data = mapB.data();
    var arrayJ = eagerJobFor(scope, data.array());
    var funcJ = eagerJobFor(scope, data.func());
    var actualType = mapB.type();
    return new MapJob(actualType, arrayJ, funcJ, nal.loc(), scope, this);
  }

  // Order

  private Job orderLazy(OrderB order, List<Job> scope) {
    var nal = nalFor(order);
    return new LazyJob(order.type(), nal.loc(), () -> orderEager(order, nal, scope));
  }

  private Job orderEager(OrderB order, List<Job> scope) {
    var nal = nalFor(order);
    return orderEager(order, nal, scope);
  }

  private Task orderEager(OrderB order, Nal nal, List<Job> scope) {
    var type = order.type();
    var elemJs = map(order.elems(), e -> eagerJobFor(scope, e));
    var info = new TaskInfo(ORDER, nal);
    return orderEager(type, elemJs, info);
  }

  public Task orderEager(ArrayTB arrayTB, ImmutableList<Job> elemJs, TaskInfo info) {
    var convertedElemJs = convertIfNeeded(arrayTB.elem(), elemJs);
    var algorithm = new OrderAlgorithm(arrayTB);
    return taskCreator.newTask(algorithm, convertedElemJs, info);
  }

  // ParamRef

  private Job paramRefLazy(ParamRefB paramRef, List<Job> scope) {
    return scope.get(paramRef.value().intValue());
  }

  // Select

  private Job selectLazy(SelectB select, List<Job> scope) {
    var nal = nalFor(select);
    return new LazyJob(select.type(), nal.loc(), () -> selectEager(select, nal, scope));
  }

  private Job selectEager(SelectB select, List<Job> scope) {
    var nal = nalFor(select);
    return selectEager(select, nal, scope);
  }

  private Job selectEager(
      SelectB select, Nal nal, List<Job> scope) {
    var data = select.data();
    var selectableJ = eagerJobFor(scope, data.selectable());
    var indexJ = eagerJobFor(scope, data.index());
    var algorithmT = ((TupleTB) selectableJ.type()).items().get(data.index().toJ().intValue());
    var algorithm = new SelectAlgorithm(algorithmT);
    var info = new TaskInfo(SELECT, nal);
    var task = taskCreator.newTask(algorithm, list(selectableJ, indexJ), info);
    return convertIfNeeded(select.type(), nal.loc(), task);
  }

  // Value

  private Job constLazy(CnstB cnst, List<Job> scope) {
    var nal = nalFor(cnst);
    var loc = nal.loc();
    return new LazyJob(cnst.cat(), loc, () -> new ConstTask(cnst, nal));
  }

  private Job constEager(CnstB cnst, List<Job> scope) {
    var nal = nalFor(cnst);
    return new ConstTask(cnst, nal);
  }

  // helper methods

  public Job callFuncEagerJob(TypeB actualEvalT, FuncB func, ImmutableList<Job> args, Loc loc,
      List<Job> scope) {
    var job = eagerJobFor(args, func.body());
    var nal = nalFor(func);
    var name = nal.name() + PARENTHESES;
    var convertedJ = convertIfNeeded(actualEvalT, nal.loc(), job);
    return new VirtualTask(convertedJ, new TaskInfo(CALL, name, loc));
  }

  private ImmutableList<Job> convertJobs(
      ImmutableList<TypeB> types, Nal nal, ImmutableList<Job> jobs) {
    return zip(types, jobs, (t, j) -> convertIfNeeded(t, nal.loc(), j));
  }

  private ImmutableList<Job> convertIfNeeded(TypeB type, ImmutableList<Job> elemJs) {
    return map(elemJs, j -> convertIfNeeded(type, j.loc(), j));
  }

  private Job convertIfNeeded(TypeB type, Loc loc, Job job) {
    if (job.type().equals(type)) {
      return job;
    } else {
      var algorithm = new ConvertAlgorithm(type);
      var info = new TaskInfo(CONVERT, type.name() + " <- " + job.type().name(), loc);
      return taskCreator.newTask(algorithm, list(job), info);
    }
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
