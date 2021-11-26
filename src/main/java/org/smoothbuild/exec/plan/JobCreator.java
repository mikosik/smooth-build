package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.exec.job.TaskKind.CONSTRUCT;
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
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefinedFunctionH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IfFunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFunctionH;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.exec.algorithm.ConstAlgorithm;
import org.smoothbuild.exec.algorithm.ConstructAlgorithm;
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
    this.handler = constructHandlers();
  }

  private ImmutableMap<Class<?>, Handler<?>> constructHandlers() {
    return ImmutableMap.<Class<?>, Handler<?>>builder()
        .put(ArrayH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(BoolH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(BlobH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(CallH.class, new Handler<>(this::callLazy, this::callEager))
        .put(ConstructH.class, new Handler<>(this::constructLazy, this::constructEager))
        .put(DefinedFunctionH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(FunctionH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(IfFunctionH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(IntH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(MapFunctionH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(NativeFunctionH.class, new Handler<>(this::valueLazy, this::valueEager))
        .put(OrderH.class, new Handler<>(this::orderLazy, this::orderEager))
        .put(RefH.class, new Handler<>(this::paramRefLazy, this::paramRefLazy))
        .put(SelectH.class, new Handler<>(this::selectLazy, this::selectEager))
        .put(StringH.class, new Handler<>(this::valueLazy, this::valueEager))
        .build();
  }

  private ImmutableList<Job> eagerJobsFor(IndexedScope<Job> scope, BoundsMap<TypeHV> variables,
      ImmutableList<? extends ObjectH> objs) {
    return map(objs, e -> eagerJobFor(scope, variables, e));
  }

  public Job jobFor(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, ObjectH expr,
      boolean eager) {
    return handlerFor(expr).job(eager).apply(scope, vars, expr);
  }

  public Job eagerJobFor(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, ObjectH expr) {
    return handlerFor(expr).eagerJob().apply(scope, vars, expr);
  }

  private Job lazyJobFor(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, ObjectH expr) {
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

  private Job callLazy(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, CallH call) {
    return callJob(scope, vars, call, false);
  }

  private Job callEager(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, CallH call) {
    return callJob(scope, vars, call, true);
  }

  private Job callJob(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, CallH call, boolean eager) {
    var callData = call.data();
    var functionJ = jobFor(scope, vars, callData.function(), eager);
    var argumentsJ = map(callData.arguments().items(), a -> lazyJobFor(scope, vars, a));
    var location = nals.get(call).location();
    var actualArgumentTypes =
        map(argumentsJ, a -> typing.mapVariables(a.type(), vars, factory.lower()));
    var newVariables = inferVariablesInFunctionCall(functionJ, actualArgumentTypes);
    return callJob(scope, functionJ, argumentsJ, location, newVariables, eager);
  }

  private Job callJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args, Location location,
      BoundsMap<TypeHV> vars, boolean eager) {
    if (eager) {
      return callEagerJob(scope, func, args, location, vars);
    } else {
      var functionType = (FunctionTypeH) func.type();
      var actualResultType = typing.mapVariables(functionType.result(), vars, factory.lower());
      return new LazyJob(actualResultType, location,
          () -> callEagerJob(scope, func, args, location, vars));
    }
  }

  public Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args,
      Location location) {
    var variables = inferVariablesInFunctionCall(func, args);
    return callEagerJob(scope, func, args, location, variables);
  }

  private Job callEagerJob(IndexedScope<Job> scope, Job func, ImmutableList<Job> args,
      Location location, BoundsMap<TypeHV> vars) {
    var functionType = (FunctionTypeH) func.type();
    var actualResultType = typing.mapVariables(functionType.result(), vars, factory.lower());
    return new CallJob(actualResultType, func, args, location, vars, scope, JobCreator.this);
  }

  private BoundsMap<TypeHV> inferVariablesInFunctionCall(Job func, List<Job> args) {
    var argumentTypes = map(args, Job::type);
    return inferVariablesInFunctionCall(func, argumentTypes);
  }

  private BoundsMap<TypeHV> inferVariablesInFunctionCall(Job func, ImmutableList<TypeHV> argTypes) {
    var funcType = (FunctionTypeH) func.type();
    return typing.inferVariableBounds(funcType.parameters(), argTypes, factory.lower());
  }

  // Value

  private Job valueLazy(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, ValueH val) {
    Nal nal = nals.get(val);
    var location = nal.location();
    return new LazyJob(val.type(), location, () -> valueEagerJob(nal, val));
  }

  private Job valueEager(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, ValueH val) {
    var nal = nals.get(val);
    return valueEagerJob(nal, val);
  }

  private Task valueEagerJob(Nal nal, ValueH val) {
    var info = new TaskInfo(LITERAL, nal);
    var algorithm = new ConstAlgorithm(val);
    return new Task(val.type(), list(), info, algorithm);
  }

  // Construct

  private Job constructLazy(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, ConstructH constructH) {
    var nal = nals.get(constructH);
    var location = nal.location();
    return new LazyJob(constructH.evaluationType(), location,
        () -> constructEager(scope, vars, constructH, nal));
  }

  private Job constructEager(IndexedScope<Job> scope, BoundsMap<TypeHV> vars,
      ConstructH constructH) {
    var nal = nals.get(constructH);
    return constructEager(scope, vars, constructH, nal);
  }
  private Job constructEager(IndexedScope<Job> scope, BoundsMap<TypeHV> vars,
      ConstructH constructH, Nal nal) {
    var type = constructH.evaluationType();
    var argumentsJ = eagerJobsFor(scope, vars, constructH.items());
    var info = new TaskInfo(CONSTRUCT, nal);
    var algorithm = new ConstructAlgorithm(constructH.evaluationType());
    return new Task(type, argumentsJ, info, algorithm);
  }

  // Order

  private Job orderLazy(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, OrderH orderH) {
    var nal = nals.get(orderH);
    return new LazyJob(orderH.evaluationType(), nal.location(),
        () -> orderEager(scope, vars, orderH));
  }

  private Job orderEager(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, OrderH orderH) {
    var nal = nals.get(orderH);
    return orderEager(scope, vars, orderH, nal);
  }

  private Task orderEager(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, OrderH orderH, Nal nal) {
    var type = orderH.evaluationType();
    var actualType = (ArrayTypeH) typing.mapVariables(type, vars, factory.lower());
    var elementsJ = map(orderH.elements(), e -> eagerJobFor(scope, vars, e));
    var info = new TaskInfo(LITERAL, nal);
    return orderEager(actualType, elementsJ, info);
  }

  public Task orderEager(ArrayTypeH typeHV, ImmutableList<Job> elemsJ, TaskInfo info) {
    var algorithm = new OrderAlgorithm(typeHV);
    return new Task(typeHV, elemsJ, info, algorithm);
  }

  // ParamRef

  private Job paramRefLazy(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, RefH ref) {
    return scope.get(ref.value().intValue());
  }

  // Select

  private Job selectLazy(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, SelectH select) {
    var nal = nals.get(select);
    return new LazyJob(select.evaluationType(), nal.location(),
        () -> selectEager(scope, vars, select, nal));
  }

  private Job selectEager(IndexedScope<Job> scope, BoundsMap<TypeHV> vars, SelectH select) {
    return selectEager(scope, vars, select, nals.get(select));
  }

  private Task selectEager(
      IndexedScope<Job> scope, BoundsMap<TypeHV> vars, SelectH selectH, Nal nal) {
    var data = selectH.data();
    var index = data.index();
    var algorithm = new SelectAlgorithm(index, selectH.evaluationType());
    var dependencies = list(eagerJobFor(scope, vars, data.tuple()));
    var info = new TaskInfo(SELECT, nal);
    return new Task(selectH.evaluationType(), dependencies, info, algorithm);
  }

  // helper methods

  public Job evaluateFunctionEagerJob(IndexedScope<Job> scope, BoundsMap<TypeHV> vars,
      TypeHV actualResType, FunctionH functionH, ImmutableList<Job> args,
      Location location) {
    return switch (functionH) {
      case DefinedFunctionH def -> definedFunctionEager(def, args, scope, vars, location);
      case NativeFunctionH nat -> nativeFunctionEager(nat, actualResType, args, location);
      case IfFunctionH iff -> ifFunctionEager(actualResType, args, location);
      case MapFunctionH map -> mapFunctionEager(actualResType, args, scope, location);
      default -> throw new RuntimeException();
    };
  }

  private Job definedFunctionEager(DefinedFunctionH definedFunctionH, ImmutableList<Job> args,
      IndexedScope<Job> scope, BoundsMap<TypeHV> vars, Location location) {
    var job = eagerJobFor(new IndexedScope<>(scope, args), vars, definedFunctionH.body());
    var name = nals.get(definedFunctionH).name();
    return new VirtualJob(job, new TaskInfo(CALL, name, location));
  }

  private Job nativeFunctionEager(NativeFunctionH nativeFunctionH, TypeHV actualResType,
      ImmutableList<Job> args, Location location) {
    var name = nals.get(nativeFunctionH).name();
    var algorithm = new InvokeAlgorithm(actualResType, name, nativeFunctionH, methodLoader);
    var info = new TaskInfo(CALL, name, location);
    return new Task(actualResType, args, info, algorithm);
  }

  private Job ifFunctionEager(TypeHV actualResType, ImmutableList<Job> args, Location location) {
    return new IfJob(actualResType, args, location);
  }

  private Job mapFunctionEager(TypeHV actualResType, ImmutableList<Job> args,
      IndexedScope<Job> scope, Location location) {
    return new MapJob(actualResType, location, args, scope, this);
  }

  public Job commandLineExprEagerJob(ObjectH obj) {
    return eagerJobFor(new IndexedScope<>(list()), boundsMap(), obj);
  }

  public record Handler<E>(
      TriFunction<IndexedScope<Job>, BoundsMap<TypeHV>, E, Job> lazyJob,
      TriFunction<IndexedScope<Job>, BoundsMap<TypeHV>, E, Job> eagerJob) {
    public TriFunction<IndexedScope<Job>, BoundsMap<TypeHV>, E, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}
