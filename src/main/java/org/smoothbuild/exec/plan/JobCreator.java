package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.exec.job.TaskKind.COMBINE;
import static org.smoothbuild.exec.job.TaskKind.INTERNAL;
import static org.smoothbuild.exec.job.TaskKind.LITERAL;
import static org.smoothbuild.exec.job.TaskKind.SELECT;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.MapH.MapData;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.exec.algorithm.CombineAlgorithm;
import org.smoothbuild.exec.algorithm.InvokeAlgorithm;
import org.smoothbuild.exec.algorithm.OrderAlgorithm;
import org.smoothbuild.exec.algorithm.SelectAlgorithm;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.exec.job.CallJob;
import org.smoothbuild.exec.job.ConstJob;
import org.smoothbuild.exec.job.IfJob;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.job.LazyJob;
import org.smoothbuild.exec.job.MapJob;
import org.smoothbuild.exec.job.Task;
import org.smoothbuild.exec.job.TaskInfo;
import org.smoothbuild.exec.job.VirtualJob;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.TriFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JobCreator {
  private final MethodLoader methodLoader;
  private final TypingH typing;
  private final ImmutableMap<ObjH, Nal> nals;
  private final Map<Class<?>, Handler<?>> handler;

  @Inject
  public JobCreator(MethodLoader methodLoader, TypingH typing, ImmutableMap<ObjH, Nal> nals) {
    this.methodLoader = methodLoader;
    this.typing = typing;
    this.nals = nals;
    this.handler = combineHandlers();
  }

  private ImmutableMap<Class<?>, Handler<?>> combineHandlers() {
    return ImmutableMap.<Class<?>, Handler<?>>builder()
        .put(ArrayH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(BoolH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(BlobH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(CallH.class, new Handler<>(this::callLazy, this::callEager))
        .put(CombineH.class, new Handler<>(this::combineLazy, this::combineEager))
        .put(FuncH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(IfH.class, new Handler<>(this::ifLazy, this::ifEager))
        .put(IntH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(MapH.class, new Handler<>(this::mapLazy, this::mapEager))
        .put(InvokeH.class, new Handler<>(this::invokeLazy, this::invokeEager))
        .put(OrderH.class, new Handler<>(this::orderLazy, this::orderEager))
        .put(ParamRefH.class, new Handler<>(this::paramRefLazy, this::paramRefLazy))
        .put(SelectH.class, new Handler<>(this::selectLazy, this::selectEager))
        .put(StringH.class, new Handler<>(this::valueLazy, this::valueEager))
        .build();
  }

  public Job eagerJobFor(ObjH obj) {
    return eagerJobFor(new IndexedScope<>(list()), boundsMap(), obj);
  }

  private ImmutableList<Job> eagerJobsFor(
      IndexedScope<Job> scope, BoundsMap<TypeH> vars, ImmutableList<? extends ObjH> objs) {
    return map(objs, e -> eagerJobFor(scope, vars, e));
  }

  private Job jobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjH expr,
      boolean eager) {
    return handlerFor(expr).job(eager).apply(scope, vars, expr);
  }

  private Job eagerJobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjH expr) {
    return handlerFor(expr).eagerJob().apply(scope, vars, expr);
  }

  private Job lazyJobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjH expr) {
    return handlerFor(expr).lazyJob().apply(scope, vars, expr);
  }

  private <T> Handler<T> handlerFor(ObjH obj) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) handler.get(obj.getClass());
    if (result == null) {
      System.out.println("expression.getClass() = " + obj.getClass());
    }
    return result;
  }

  // Call

  private Job callLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, CallH call) {
    return callJob(scope, vars, call, false);
  }

  private Job callEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, CallH call) {
    return callJob(scope, vars, call, true);
  }

  private Job callJob(IndexedScope<Job> scope, BoundsMap<TypeH> vars, CallH call, boolean eager) {
    var callData = call.data();
    var funcJ = jobFor(scope, vars, callData.callable(), eager);
    var argsJ = map(callData.args().items(), a -> lazyJobFor(scope, vars, a));
    var loc = nals.get(call).loc();
    var actualArgTypes = map(argsJ, a -> typing.mapVarsLower(a.type(), vars));
    var newVars = inferVarsInFuncCall(funcJ, actualArgTypes);
    return callJob(scope, funcJ, argsJ, loc, newVars, eager);
  }

  private Job callJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeH> vars, boolean eager) {
    if (eager) {
      return callEagerJob(scope, func, args, loc, vars);
    } else {
      var funcT = (FuncTH) func.type();
      var actualResT = typing.mapVarsLower(funcT.res(), vars);
      return new LazyJob(actualResT, loc, () -> callEagerJob(scope, func, args, loc, vars));
    }
  }

  public Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc) {
    var vars = inferVarsInFuncCall(func, args);
    return callEagerJob(scope, func, args, loc, vars);
  }

  private Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeH> vars) {
    var funcT = (FuncTH) func.type();
    var actualResT = typing.mapVarsLower(funcT.res(), vars);
    return new CallJob(actualResT, func, args, loc, vars, scope, JobCreator.this);
  }

  private BoundsMap<TypeH> inferVarsInFuncCall(Job func, List<Job> args) {
    var argTs = map(args, Job::type);
    return inferVarsInFuncCall(func, argTs);
  }

  private BoundsMap<TypeH> inferVarsInFuncCall(Job func, ImmutableList<TypeH> argTs) {
    var funcT = (FuncTH) func.type();
    return typing.inferVarBoundsLower(funcT.params(), argTs);
  }

  // Combine

  private Job combineLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, CombineH combine) {
    var nal = nals.get(combine);
    var loc = nal.loc();
    return new LazyJob(combine.type(), loc,
        () -> combineEager(scope, vars, combine, nal));
  }

  private Job combineEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, CombineH combine) {
    var nal = nals.get(combine);
    return combineEager(scope, vars, combine, nal);
  }
  private Job combineEager(
      IndexedScope<Job> scope, BoundsMap<TypeH> vars, CombineH combine, Nal nal) {
    var type = combine.type();
    var argsJ = eagerJobsFor(scope, vars, combine.items());
    var info = new TaskInfo(COMBINE, nal);
    var algorithm = new CombineAlgorithm(combine.type());
    return new Task(type, argsJ, info, algorithm);
  }

  // If

  private Job ifLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, IfH ifH) {
    Nal nal = nals.get(ifH);
    return new LazyJob(ifH.type(), nal.loc(),
        () -> ifEager(scope, vars, ifH, nal));
  }

  private Job ifEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, IfH ifH) {
    Nal nal = nals.get(ifH);
    return ifEager(scope, vars, ifH, nal);
  }

  private Job ifEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, IfH ifH, Nal nal) {
    var ifData = ifH.data();
    var conditionJ = eagerJobFor(scope, vars, ifData.condition());
    var thenJ = lazyJobFor(scope, vars, ifData.then_());
    var elseJ = lazyJobFor(scope, vars, ifData.else_());
    var deps = list(conditionJ, thenJ, elseJ);
    return new IfJob(ifH.type(), deps, nal.loc());
  }

  // Invoke

  private Job invokeLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, InvokeH invokeH) {
    Nal nal = nals.get(invokeH);
    return new LazyJob(invokeH.type(), nal.loc(),
        () -> invokeEager(scope, vars, invokeH, nal));
  }

  private Job invokeEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, InvokeH invokeH) {
    Nal nal = nals.get(invokeH);
    return invokeEager(scope, vars, invokeH, nal);
  }

  private Task invokeEager(
      IndexedScope<Job> scope, BoundsMap<TypeH> vars, InvokeH invokeH, Nal nal) {
    var name = nal.name();
    var actualType = typing.mapVarsLower(invokeH.type(), vars);
    var algorithm = new InvokeAlgorithm(actualType, name, invokeH.method(), methodLoader);
    var info = new TaskInfo(INTERNAL, name, nal.loc());
    var argsJ = eagerJobsFor(scope, vars, invokeH.args().items());
    return new Task(actualType, argsJ, info, algorithm);
  }

  // Map

  private Job mapLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, MapH mapH) {
    Nal nal = nals.get(mapH);
    return new LazyJob(mapH.type(), nal.loc(),
        () -> mapEager(scope, vars, mapH, nal));
  }

  private Job mapEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, MapH mapH) {
    Nal nal = nals.get(mapH);
    return mapEager(scope, vars, mapH, nal);
  }

  private Job mapEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, MapH mapH, Nal nal) {
    MapData data = mapH.data();
    var arrayJ = eagerJobFor(scope, vars, data.array());
    var funcJ = eagerJobFor(scope, vars, data.func());
    var deps = list(arrayJ, funcJ);
    TypeH actualType = typing.mapVarsLower(mapH.type(), vars);
    return new MapJob(actualType, nal.loc(), deps, scope, this);
  }

  // Order

  private Job orderLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, OrderH order) {
    var nal = nals.get(order);
    return new LazyJob(order.type(), nal.loc(),
        () -> orderEager(scope, vars, order));
  }

  private Job orderEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, OrderH order) {
    var nal = nals.get(order);
    return orderEager(scope, vars, order, nal);
  }

  private Task orderEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, OrderH order, Nal nal) {
    var type = order.type();
    var actualT = (ArrayTH) typing.mapVarsLower(type, vars);
    var elemsJ = map(order.elems(), e -> eagerJobFor(scope, vars, e));
    var info = new TaskInfo(LITERAL, nal);
    return orderEager(actualT, elemsJ, info);
  }

  public Task orderEager(ArrayTH arrayTH, ImmutableList<Job> elemsJ, TaskInfo info) {
    var algorithm = new OrderAlgorithm(arrayTH);
    return new Task(arrayTH, elemsJ, info, algorithm);
  }

  // ParamRef

  private Job paramRefLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ParamRefH paramRef) {
    return scope.get(paramRef.value().intValue());
  }

  // Select

  private Job selectLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, SelectH select) {
    var nal = nals.get(select);
    return new LazyJob(select.type(), nal.loc(),
        () -> selectEager(scope, vars, select, nal));
  }

  private Job selectEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, SelectH select) {
    return selectEager(scope, vars, select, nals.get(select));
  }

  private Task selectEager(
      IndexedScope<Job> scope, BoundsMap<TypeH> vars, SelectH selectH, Nal nal) {
    var data = selectH.data();
    var algorithm = new SelectAlgorithm(selectH.type());
    var selectable = eagerJobFor(scope, vars, data.selectable());
    var index = eagerJobFor(data.index());
    var info = new TaskInfo(SELECT, nal);
    return new Task(selectH.type(), list(selectable, index), info, algorithm);
  }

  // Value

  private Job valueLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ValH val) {
    var nal = nals.get(val);
    var loc = nal.loc();
    return new LazyJob(val.cat(), loc, () -> new ConstJob(val, nal));
  }

  private Job valueEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ValH val) {
    var nal = nals.get(val);
    return new ConstJob(val, nal);
  }

  // helper methods

  public Job callFuncEagerJob(IndexedScope<Job> scope, BoundsMap<TypeH> vars,
      TypeH actualResT, FuncH func, ImmutableList<Job> args, Loc loc) {
    var job = eagerJobFor(new IndexedScope<>(scope, args), vars, func.body());
    var name = nals.get(func).name();
    return new VirtualJob(job, new TaskInfo(CALL, name, loc));
  }

  public record Handler<E>(
      TriFunction<IndexedScope<Job>, BoundsMap<TypeH>, E, Job> lazyJob,
      TriFunction<IndexedScope<Job>, BoundsMap<TypeH>, E, Job> eagerJob) {
    public TriFunction<IndexedScope<Job>, BoundsMap<TypeH>, E, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}
