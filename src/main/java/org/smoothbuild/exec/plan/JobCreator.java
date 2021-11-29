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

import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
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
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
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
import org.smoothbuild.lang.base.define.Location;
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
  private final ImmutableMap<ObjectH, Nal> nals;
  private final Map<Class<?>, Handler<?>> handler;

  @Inject
  public JobCreator(MethodLoader methodLoader, TypeFactoryH factory, TypingH typing,
      ImmutableMap<ObjectH, Nal> nals) {
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
        .put(RefH.class, new Handler<>(this::paramRefLazy, this::paramRefLazy))
        .put(SelectH.class, new Handler<>(this::selectLazy, this::selectEager))
        .put(StringH.class, new Handler<>(this::valueLazy, this::valueEager))
        .build();
  }

  private ImmutableList<Job> eagerJobsFor(IndexedScope<Job> scope, BoundsMap<TypeH> variables,
      ImmutableList<? extends ObjectH> objs) {
    return map(objs, e -> eagerJobFor(scope, variables, e));
  }

  public Job jobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjectH expr,
      boolean eager) {
    return handlerFor(expr).job(eager).apply(scope, vars, expr);
  }

  public Job eagerJobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjectH expr) {
    return handlerFor(expr).eagerJob().apply(scope, vars, expr);
  }

  private Job lazyJobFor(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ObjectH expr) {
    return handlerFor(expr).lazyJob().apply(scope, vars, expr);
  }

  public <T> Handler<T> handlerFor(ObjectH expr) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) handler.get(expr.getClass());
    if (result == null) {
      System.out.println("expression.getClass() = " + expr.getClass());
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
    var funcJ = jobFor(scope, vars, callData.func(), eager);
    var argsJ = map(callData.args().items(), a -> lazyJobFor(scope, vars, a));
    var location = nals.get(call).location();
    var actualArgTypes =
        map(argsJ, a -> typing.mapVariables(a.type(), vars, factory.lower()));
    var newVariables = inferVariablesInFuncCall(funcJ, actualArgTypes);
    return callJob(scope, funcJ, argsJ, location, newVariables, eager);
  }

  private Job callJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Location location,
      BoundsMap<TypeH> vars, boolean eager) {
    if (eager) {
      return callEagerJob(scope, func, args, location, vars);
    } else {
      var funcType = (FuncTypeH) func.type();
      var actualResultType = typing.mapVariables(funcType.result(), vars, factory.lower());
      return new LazyJob(actualResultType, location,
          () -> callEagerJob(scope, func, args, location, vars));
    }
  }

  public Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args,
      Location location) {
    var variables = inferVariablesInFuncCall(func, args);
    return callEagerJob(scope, func, args, location, variables);
  }

  private Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args,
      Location location, BoundsMap<TypeH> vars) {
    var funcType = (FuncTypeH) func.type();
    var actualResultType = typing.mapVariables(funcType.result(), vars, factory.lower());
    return new CallJob(actualResultType, func, args, location, vars, scope, JobCreator.this);
  }

  private BoundsMap<TypeH> inferVariablesInFuncCall(Job func, List<Job> args) {
    var argTypes = map(args, Job::type);
    return inferVariablesInFuncCall(func, argTypes);
  }

  private BoundsMap<TypeH> inferVariablesInFuncCall(Job func, ImmutableList<TypeH> argTypes) {
    var funcType = (FuncTypeH) func.type();
    return typing.inferVariableBounds(funcType.params(), argTypes, factory.lower());
  }

  // Value

  private Job valueLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ValueH val) {
    Nal nal = nals.get(val);
    var location = nal.location();
    return new LazyJob(val.spec(), location, () -> valueEagerJob(nal, val));
  }

  private Job valueEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, ValueH val) {
    var nal = nals.get(val);
    return valueEagerJob(nal, val);
  }

  private Task valueEagerJob(Nal nal, ValueH val) {
    var info = new TaskInfo(LITERAL, nal);
    var algorithm = new ConstAlgorithm(val);
    return new Task(val.spec(), list(), info, algorithm);
  }

  // Combine

  private Job combineLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, CombineH combineH) {
    var nal = nals.get(combineH);
    var location = nal.location();
    return new LazyJob(combineH.type(), location,
        () -> combineEager(scope, vars, combineH, nal));
  }

  private Job combineEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars,
      CombineH combineH) {
    var nal = nals.get(combineH);
    return combineEager(scope, vars, combineH, nal);
  }
  private Job combineEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars,
      CombineH combineH, Nal nal) {
    var type = combineH.type();
    var argsJ = eagerJobsFor(scope, vars, combineH.items());
    var info = new TaskInfo(COMBINE, nal);
    var algorithm = new CombineAlgorithm(combineH.type());
    return new Task(type, argsJ, info, algorithm);
  }

  // Order

  private Job orderLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, OrderH orderH) {
    var nal = nals.get(orderH);
    return new LazyJob(orderH.type(), nal.location(),
        () -> orderEager(scope, vars, orderH));
  }

  private Job orderEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, OrderH orderH) {
    var nal = nals.get(orderH);
    return orderEager(scope, vars, orderH, nal);
  }

  private Task orderEager(IndexedScope<Job> scope, BoundsMap<TypeH> vars, OrderH orderH, Nal nal) {
    var type = orderH.type();
    var actualType = (ArrayTypeH) typing.mapVariables(type, vars, factory.lower());
    var elemsJ = map(orderH.elems(), e -> eagerJobFor(scope, vars, e));
    var info = new TaskInfo(LITERAL, nal);
    return orderEager(actualType, elemsJ, info);
  }

  public Task orderEager(ArrayTypeH typeHV, ImmutableList<Job> elemsJ, TaskInfo info) {
    var algorithm = new OrderAlgorithm(typeHV);
    return new Task(typeHV, elemsJ, info, algorithm);
  }

  // ParamRef

  private Job paramRefLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, RefH ref) {
    return scope.get(ref.value().intValue());
  }

  // Select

  private Job selectLazy(IndexedScope<Job> scope, BoundsMap<TypeH> vars, SelectH select) {
    var nal = nals.get(select);
    return new LazyJob(select.type(), nal.location(),
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
    var dependencies = list(eagerJobFor(scope, vars, data.tuple()));
    var info = new TaskInfo(SELECT, nal);
    return new Task(selectH.type(), dependencies, info, algorithm);
  }

  // helper methods

  public Job evaluateFuncEagerJob(IndexedScope<Job> scope, BoundsMap<TypeH> vars,
      TypeH actualResType, FuncH funcH, ImmutableList<Job> args,
      Location location) {
    return switch (funcH) {
      case DefFuncH def -> defFuncEager(def, args, scope, vars, location);
      case NatFuncH nat -> natFuncEager(nat, actualResType, args, location);
      case IfFuncH iff -> ifFuncEager(actualResType, args, location);
      case MapFuncH map -> mapFuncEager(actualResType, args, scope, location);
      default -> throw new RuntimeException();
    };
  }

  private Job defFuncEager(DefFuncH defFuncH, ImmutableList<Job> args,
      IndexedScope<Job> scope, BoundsMap<TypeH> vars, Location location) {
    var job = eagerJobFor(new IndexedScope<>(scope, args), vars, defFuncH.body());
    var name = nals.get(defFuncH).name();
    return new VirtualJob(job, new TaskInfo(CALL, name, location));
  }

  private Job natFuncEager(NatFuncH natFuncH, TypeH actualResType,
      ImmutableList<Job> args, Location location) {
    var name = nals.get(natFuncH).name();
    var algorithm = new InvokeAlgorithm(actualResType, name, natFuncH, methodLoader);
    var info = new TaskInfo(CALL, name, location);
    return new Task(actualResType, args, info, algorithm);
  }

  private Job ifFuncEager(TypeH actualResType, ImmutableList<Job> args, Location location) {
    return new IfJob(actualResType, args, location);
  }

  private Job mapFuncEager(TypeH actualResType, ImmutableList<Job> args,
      IndexedScope<Job> scope, Location location) {
    return new MapJob(actualResType, location, args, scope, this);
  }

  public Job commandLineExprEagerJob(ObjectH obj) {
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
