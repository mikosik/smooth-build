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

import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.expr.CallB;
import org.smoothbuild.db.object.obj.expr.CombineB;
import org.smoothbuild.db.object.obj.expr.IfB;
import org.smoothbuild.db.object.obj.expr.InvokeB;
import org.smoothbuild.db.object.obj.expr.MapB;
import org.smoothbuild.db.object.obj.expr.OrderB;
import org.smoothbuild.db.object.obj.expr.ParamRefB;
import org.smoothbuild.db.object.obj.expr.SelectB;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.obj.val.BoolB;
import org.smoothbuild.db.object.obj.val.FuncB;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.db.object.type.TypingB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.db.object.type.val.FuncTB;
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
  private final TypingB typing;
  private final ImmutableMap<ObjB, Nal> nals;
  private final Map<Class<?>, Handler<?>> handler;

  @Inject
  public JobCreator(MethodLoader methodLoader, TypingB typing, ImmutableMap<ObjB, Nal> nals) {
    this.methodLoader = methodLoader;
    this.typing = typing;
    this.nals = nals;
    this.handler = combineHandlers();
  }

  private ImmutableMap<Class<?>, Handler<?>> combineHandlers() {
    return ImmutableMap.<Class<?>, Handler<?>>builder()
        .put(ArrayB.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(BoolB.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(BlobB.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(CallB.class, new Handler<>(this::callLazy, this::callEager))
        .put(CombineB.class, new Handler<>(this::combineLazy, this::combineEager))
        .put(FuncB.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(IfB.class, new Handler<>(this::ifLazy, this::ifEager))
        .put(IntB.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(MapB.class, new Handler<>(this::mapLazy, this::mapEager))
        .put(InvokeB.class, new Handler<>(this::invokeLazy, this::invokeEager))
        .put(OrderB.class, new Handler<>(this::orderLazy, this::orderEager))
        .put(ParamRefB.class, new Handler<>(this::paramRefLazy, this::paramRefLazy))
        .put(SelectB.class, new Handler<>(this::selectLazy, this::selectEager))
        .put(StringB.class, new Handler<>(this::valueLazy, this::valueEager))
        .build();
  }

  public Job eagerJobFor(ObjB obj) {
    return eagerJobFor(new IndexedScope<>(list()), boundsMap(), obj);
  }

  private ImmutableList<Job> eagerJobsFor(
      IndexedScope<Job> scope, BoundsMap<TypeB> vars, ImmutableList<? extends ObjB> objs) {
    return map(objs, e -> eagerJobFor(scope, vars, e));
  }

  private Job jobFor(IndexedScope<Job> scope, BoundsMap<TypeB> vars, ObjB expr,
      boolean eager) {
    return handlerFor(expr).job(eager).apply(scope, vars, expr);
  }

  private Job eagerJobFor(IndexedScope<Job> scope, BoundsMap<TypeB> vars, ObjB expr) {
    return handlerFor(expr).eagerJob().apply(scope, vars, expr);
  }

  private Job lazyJobFor(IndexedScope<Job> scope, BoundsMap<TypeB> vars, ObjB expr) {
    return handlerFor(expr).lazyJob().apply(scope, vars, expr);
  }

  private <T> Handler<T> handlerFor(ObjB obj) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) handler.get(obj.getClass());
    if (result == null) {
      System.out.println("expression.getClass() = " + obj.getClass());
    }
    return result;
  }

  // Call

  private Job callLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, CallB call) {
    return callJob(scope, vars, call, false);
  }

  private Job callEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, CallB call) {
    return callJob(scope, vars, call, true);
  }

  private Job callJob(IndexedScope<Job> scope, BoundsMap<TypeB> vars, CallB call, boolean eager) {
    var callData = call.data();
    var funcJ = jobFor(scope, vars, callData.callable(), eager);
    var argsJ = map(callData.args().items(), a -> lazyJobFor(scope, vars, a));
    var loc = nals.get(call).loc();
    var newVars = inferVarsInFuncCall(funcJ, map(argsJ, Job::type));
    return callJob(scope, funcJ, argsJ, loc, newVars, eager);
  }

  private Job callJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeB> vars, boolean eager) {
    if (eager) {
      return callEagerJob(scope, func, args, loc, vars);
    } else {
      var funcT = (FuncTB) func.type();
      var actualResT = typing.mapVarsLower(funcT.res(), vars);
      return new LazyJob(actualResT, loc, () -> callEagerJob(scope, func, args, loc, vars));
    }
  }

  public Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc) {
    var vars = inferVarsInFuncCall(func, args);
    return callEagerJob(scope, func, args, loc, vars);
  }

  private Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeB> vars) {
    var funcT = (FuncTB) func.type();
    var actualResT = typing.mapVarsLower(funcT.res(), vars);
    return new CallJob(actualResT, func, args, loc, vars, scope, JobCreator.this);
  }

  private BoundsMap<TypeB> inferVarsInFuncCall(Job func, List<Job> args) {
    var argTs = map(args, Job::type);
    return inferVarsInFuncCall(func, argTs);
  }

  private BoundsMap<TypeB> inferVarsInFuncCall(Job func, ImmutableList<TypeB> argTs) {
    var funcT = (FuncTB) func.type();
    return typing.inferVarBoundsLower(funcT.params(), argTs);
  }

  // Combine

  private Job combineLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, CombineB combine) {
    var nal = nals.get(combine);
    var loc = nal.loc();
    return new LazyJob(combine.type(), loc,
        () -> combineEager(scope, vars, combine, nal));
  }

  private Job combineEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, CombineB combine) {
    var nal = nals.get(combine);
    return combineEager(scope, vars, combine, nal);
  }
  private Job combineEager(
      IndexedScope<Job> scope, BoundsMap<TypeB> vars, CombineB combine, Nal nal) {
    var type = combine.type();
    var argsJ = eagerJobsFor(scope, vars, combine.items());
    var info = new TaskInfo(COMBINE, nal);
    var algorithm = new CombineAlgorithm(combine.type());
    return new Task(type, argsJ, info, algorithm);
  }

  // If

  private Job ifLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, IfB ifB) {
    Nal nal = nals.get(ifB);
    return new LazyJob(ifB.type(), nal.loc(),
        () -> ifEager(scope, vars, ifB, nal));
  }

  private Job ifEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, IfB ifB) {
    Nal nal = nals.get(ifB);
    return ifEager(scope, vars, ifB, nal);
  }

  private Job ifEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, IfB ifB, Nal nal) {
    var ifData = ifB.data();
    var conditionJ = eagerJobFor(scope, vars, ifData.condition());
    var thenJ = lazyJobFor(scope, vars, ifData.then());
    var elseJ = lazyJobFor(scope, vars, ifData.else_());
    return new IfJob(ifB.type(), conditionJ, thenJ, elseJ, nal.loc());
  }

  // Invoke

  private Job invokeLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, InvokeB invokeB) {
    Nal nal = nals.get(invokeB);
    return new LazyJob(invokeB.type(), nal.loc(),
        () -> invokeEager(scope, vars, invokeB, nal));
  }

  private Job invokeEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, InvokeB invokeB) {
    Nal nal = nals.get(invokeB);
    return invokeEager(scope, vars, invokeB, nal);
  }

  private Task invokeEager(
      IndexedScope<Job> scope, BoundsMap<TypeB> vars, InvokeB invokeB, Nal nal) {
    var name = nal.name();
    var actualType = typing.mapVarsLower(invokeB.type(), vars);
    var invokeData = invokeB.data();
    var algorithm = new InvokeAlgorithm(actualType, name, invokeData.method(), methodLoader);
    var info = new TaskInfo(INTERNAL, name, nal.loc());
    var argsJ = eagerJobsFor(scope, vars, invokeData.args().items());
    return new Task(actualType, argsJ, info, algorithm);
  }

  // Map

  private Job mapLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, MapB mapB) {
    Nal nal = nals.get(mapB);
    return new LazyJob(mapB.type(), nal.loc(),
        () -> mapEager(scope, vars, mapB, nal));
  }

  private Job mapEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, MapB mapB) {
    Nal nal = nals.get(mapB);
    return mapEager(scope, vars, mapB, nal);
  }

  private Job mapEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, MapB mapB, Nal nal) {
    MapB.Data data = mapB.data();
    var arrayJ = eagerJobFor(scope, vars, data.array());
    var funcJ = eagerJobFor(scope, vars, data.func());
    TypeB actualType = typing.mapVarsLower(mapB.type(), vars);
    return new MapJob(actualType, arrayJ, funcJ, nal.loc(), scope, this);
  }

  // Order

  private Job orderLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, OrderB order) {
    var nal = nals.get(order);
    return new LazyJob(order.type(), nal.loc(),
        () -> orderEager(scope, vars, order));
  }

  private Job orderEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, OrderB order) {
    var nal = nals.get(order);
    return orderEager(scope, vars, order, nal);
  }

  private Task orderEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, OrderB order, Nal nal) {
    var type = order.type();
    var actualT = (ArrayTB) typing.mapVarsLower(type, vars);
    var elemsJ = map(order.elems(), e -> eagerJobFor(scope, vars, e));
    var info = new TaskInfo(LITERAL, nal);
    return orderEager(actualT, elemsJ, info);
  }

  public Task orderEager(ArrayTB arrayTB, ImmutableList<Job> elemsJ, TaskInfo info) {
    var algorithm = new OrderAlgorithm(arrayTB);
    return new Task(arrayTB, elemsJ, info, algorithm);
  }

  // ParamRef

  private Job paramRefLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, ParamRefB paramRef) {
    return scope.get(paramRef.value().intValue());
  }

  // Select

  private Job selectLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, SelectB select) {
    var nal = nals.get(select);
    return new LazyJob(select.type(), nal.loc(),
        () -> selectEager(scope, vars, select, nal));
  }

  private Job selectEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, SelectB select) {
    return selectEager(scope, vars, select, nals.get(select));
  }

  private Task selectEager(
      IndexedScope<Job> scope, BoundsMap<TypeB> vars, SelectB selectB, Nal nal) {
    var data = selectB.data();
    var algorithm = new SelectAlgorithm(selectB.type());
    var selectable = eagerJobFor(scope, vars, data.selectable());
    var index = eagerJobFor(data.index());
    var info = new TaskInfo(SELECT, nal);
    return new Task(selectB.type(), list(selectable, index), info, algorithm);
  }

  // Value

  private Job valueLazy(IndexedScope<Job> scope, BoundsMap<TypeB> vars, ValB val) {
    var nal = nals.get(val);
    var loc = nal.loc();
    return new LazyJob(val.cat(), loc, () -> new ConstJob(val, nal));
  }

  private Job valueEager(IndexedScope<Job> scope, BoundsMap<TypeB> vars, ValB val) {
    var nal = nals.get(val);
    return new ConstJob(val, nal);
  }

  // helper methods

  public Job callFuncEagerJob(IndexedScope<Job> scope, BoundsMap<TypeB> vars,
      TypeB actualResT, FuncB func, ImmutableList<Job> args, Loc loc) {
    var job = eagerJobFor(new IndexedScope<>(scope, args), vars, func.body());
    var name = nals.get(func).name();
    return new VirtualJob(job, new TaskInfo(CALL, name, loc));
  }

  public record Handler<E>(
      TriFunction<IndexedScope<Job>, BoundsMap<TypeB>, E, Job> lazyJob,
      TriFunction<IndexedScope<Job>, BoundsMap<TypeB>, E, Job> eagerJob) {
    public TriFunction<IndexedScope<Job>, BoundsMap<TypeB>, E, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}
