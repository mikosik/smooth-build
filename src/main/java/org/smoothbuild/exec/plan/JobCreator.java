package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.exec.job.TaskKind.COMBINE;
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
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefFuncH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IfFuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFuncH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.exec.algorithm.CombineAlgorithm;
import org.smoothbuild.exec.algorithm.ConstAlgorithm;
import org.smoothbuild.exec.algorithm.InvokeAlgorithm;
import org.smoothbuild.exec.algorithm.OrderAlgorithm;
import org.smoothbuild.exec.algorithm.SelectAlgorithm;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.exec.job.CallJob;
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
  private final TypeFactoryH factory;
  private final TypingH typing;
  private final ImmutableMap<ObjH, Nal> nals;
  private final Map<Class<?>, Handler<?>> handler;

  @Inject
  public JobCreator(MethodLoader methodLoader, TypeFactoryH factory, TypingH typing,
      ImmutableMap<ObjH, Nal> nals) {
    this.methodLoader = methodLoader;
    this.factory = factory;
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
        .put(DefFuncH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(FuncH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(IfFuncH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(IntH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(MapFuncH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(NatFuncH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(OrderH.class, new Handler<>(this::orderLazy, this::orderEager))
        .put(ParamRefH.class, new Handler<>(this::paramRefLazy, this::paramRefLazy))
        .put(SelectH.class, new Handler<>(this::selectLazy, this::selectEager))
        .put(StringH.class, new Handler<>(this::valueLazy, this::valueEager))
        .build();
  }

  private ImmutableList<Job> eagerJobsFor(
      IndexedScope<Job> scope, BoundsMap<TypeH> vars, ImmutableList<ObjH> objs) {
    return map(objs, e -> eagerJobFor(scope, vars, e));
  }

  public Job jobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjH expr,
      boolean eager) {
    return handlerFor(expr).job(eager).apply(scope, vars, expr);
  }

  public Job eagerJobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjH expr) {
    return handlerFor(expr).eagerJob().apply(scope, vars, expr);
  }

  private Job lazyJobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjH expr) {
    return handlerFor(expr).lazyJob().apply(scope, vars, expr);
  }

  public <T> Handler<T> handlerFor(ObjH obj) {
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
    var actualArgTypes = map(argsJ, a -> typing.mapVars(a.type(), vars, factory.lower()));
    var newVars = inferVarsInFuncCall(funcJ, actualArgTypes);
    return callJob(scope, funcJ, argsJ, loc, newVars, eager);
  }

  private Job callJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeH> vars, boolean eager) {
    if (eager) {
      return callEagerJob(scope, func, args, loc, vars);
    } else {
      var funcType = (FuncTH) func.type();
      var actualResultType = typing.mapVars(funcType.res(), vars, factory.lower());
      return new LazyJob(actualResultType, loc,
          () -> callEagerJob(scope, func, args, loc, vars));
    }
  }

  public Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc) {
    var vars = inferVarsInFuncCall(func, args);
    return callEagerJob(scope, func, args, loc, vars);
  }

  private Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeH> vars) {
    var funcType = (FuncTH) func.type();
    var actualResultType = typing.mapVars(funcType.res(), vars, factory.lower());
    return new CallJob(actualResultType, func, args, loc, vars, scope, JobCreator.this);
  }

  private BoundsMap<TypeH> inferVarsInFuncCall(Job func, List<Job> args) {
    var argTypes = map(args, Job::type);
    return inferVarsInFuncCall(func, argTypes);
  }

  private BoundsMap<TypeH> inferVarsInFuncCall(Job func, ImmutableList<TypeH> argTypes) {
    var funcType = (FuncTH) func.type();
    return typing.inferVarBounds(funcType.params(), argTypes, factory.lower());
  }

  // Value

  private Job valueLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ValH val) {
    Nal nal = nals.get(val);
    var loc = nal.loc();
    return new LazyJob(val.cat(), loc, () -> valueEagerJob(nal, val));
  }

  private Job valueEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ValH val) {
    var nal = nals.get(val);
    return valueEagerJob(nal, val);
  }

  private Task valueEagerJob(Nal nal, ValH val) {
    var info = new TaskInfo(LITERAL, nal);
    var algorithm = new ConstAlgorithm(val);
    return new Task(val.cat(), list(), info, algorithm);
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
    var actualType = (ArrayTH) typing.mapVars(type, vars, factory.lower());
    var elemsJ = map(order.elems(), e -> eagerJobFor(scope, vars, e));
    var info = new TaskInfo(LITERAL, nal);
    return orderEager(actualType, elemsJ, info);
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
    var index = data.index();
    var algorithm = new SelectAlgorithm(index, selectH.type());
    var deps = list(eagerJobFor(scope, vars, data.selectable()));
    var info = new TaskInfo(SELECT, nal);
    return new Task(selectH.type(), deps, info, algorithm);
  }

  // helper methods

  public Job evaluateFuncEagerJob(IndexedScope<Job> scope, BoundsMap<TypeH> vars,
      TypeH actualResType, FuncH func, ImmutableList<Job> args, Loc loc) {
    return switch (func) {
      case DefFuncH def -> defFuncEager(def, args, scope, vars, loc);
      case NatFuncH nat -> natFuncEager(nat, actualResType, args, loc);
      case IfFuncH iff -> ifFuncEager(actualResType, args, loc);
      case MapFuncH map -> mapFuncEager(actualResType, args, scope, loc);
    };
  }

  private Job defFuncEager(DefFuncH defFuncH, ImmutableList<Job> args,
      IndexedScope<Job> scope, BoundsMap<TypeH> vars, Loc loc) {
    var job = eagerJobFor(new IndexedScope<>(scope, args), vars, defFuncH.body());
    var name = nals.get(defFuncH).name();
    return new VirtualJob(job, new TaskInfo(CALL, name, loc));
  }

  private Job natFuncEager(NatFuncH func, TypeH actualResType, ImmutableList<Job> args, Loc loc) {
    var name = nals.get(func).name();
    var algorithm = new InvokeAlgorithm(actualResType, name, func, methodLoader);
    var info = new TaskInfo(CALL, name, loc);
    return new Task(actualResType, args, info, algorithm);
  }

  private Job ifFuncEager(TypeH actualResType, ImmutableList<Job> args, Loc loc) {
    return new IfJob(actualResType, args, loc);
  }

  private Job mapFuncEager(TypeH actualResType, ImmutableList<Job> args, IndexedScope<Job> scope,
      Loc loc) {
    return new MapJob(actualResType, loc, args, scope, this);
  }

  public Job commandLineExprEagerJob(ObjH obj) {
    return eagerJobFor(new IndexedScope<>(list()), boundsMap(), obj);
  }

  public record Handler<E>(
      TriFunction<IndexedScope<Job>, BoundsMap<TypeH>, E, Job> lazyJob,
      TriFunction<IndexedScope<Job>, BoundsMap<TypeH>, E, Job> eagerJob) {
    public TriFunction<IndexedScope<Job>, BoundsMap<TypeH>, E, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}
