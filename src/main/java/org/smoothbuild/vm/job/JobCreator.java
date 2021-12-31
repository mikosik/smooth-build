package org.smoothbuild.vm.job;

import static org.smoothbuild.lang.base.type.api.VarBounds.varBounds;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.vm.job.job.TaskKind.CALL;
import static org.smoothbuild.vm.job.job.TaskKind.COMBINE;
import static org.smoothbuild.vm.job.job.TaskKind.INTERNAL;
import static org.smoothbuild.vm.job.job.TaskKind.LITERAL;
import static org.smoothbuild.vm.job.job.TaskKind.SELECT;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.api.VarBounds;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.TriFunction;
import org.smoothbuild.vm.java.MethodLoader;
import org.smoothbuild.vm.job.algorithm.CombineAlgorithm;
import org.smoothbuild.vm.job.algorithm.ConvertAlgorithm;
import org.smoothbuild.vm.job.algorithm.InvokeAlgorithm;
import org.smoothbuild.vm.job.algorithm.OrderAlgorithm;
import org.smoothbuild.vm.job.algorithm.SelectAlgorithm;
import org.smoothbuild.vm.job.job.CallJob;
import org.smoothbuild.vm.job.job.IfJob;
import org.smoothbuild.vm.job.job.Job;
import org.smoothbuild.vm.job.job.LazyJob;
import org.smoothbuild.vm.job.job.MapJob;
import org.smoothbuild.vm.job.job.Task;
import org.smoothbuild.vm.job.job.TaskInfo;
import org.smoothbuild.vm.job.job.ValJob;
import org.smoothbuild.vm.job.job.VirtualJob;

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
    this.handler = createHandlers();
  }

  private ImmutableMap<Class<?>, Handler<?>> createHandlers() {
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
    return eagerJobFor(new IndexedScope<>(list()), varBounds(), obj);
  }

  private ImmutableList<Job> eagerJobsFor(
      IndexedScope<Job> scope, VarBounds<TypeB> vars, ImmutableList<? extends ObjB> objs) {
    return map(objs, e -> eagerJobFor(scope, vars, e));
  }

  private Job jobFor(IndexedScope<Job> scope, VarBounds<TypeB> vars, ObjB expr,
      boolean eager) {
    return handlerFor(expr).job(eager).apply(expr, scope, vars);
  }

  private Job eagerJobFor(IndexedScope<Job> scope, VarBounds<TypeB> vars, ObjB expr) {
    return handlerFor(expr).eagerJob().apply(expr, scope, vars);
  }

  private Job lazyJobFor(IndexedScope<Job> scope, VarBounds<TypeB> vars, ObjB expr) {
    return handlerFor(expr).lazyJob().apply(expr, scope, vars);
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

  private Job callLazy(CallB call, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    return callJob(call, false, scope, vars);
  }

  private Job callEager(CallB call, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    return callJob(call, true, scope, vars);
  }

  private Job callJob(CallB call, boolean eager, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var callData = call.data();
    var funcJ = jobFor(scope, vars, callData.callable(), eager);
    var argsJ = map(callData.args().items(), a -> lazyJobFor(scope, vars, a));
    var loc = nals.get(call).loc();
    var newVars = inferVarsInCall(funcJ, map(argsJ, Job::type));
    var evalT = typing.mapVarsLower(call.type(), vars);
    return callJob(evalT, funcJ, argsJ, loc, eager, scope, newVars);
  }

  private Job callJob(TypeB evalT, Job func, ImmutableList<Job> args, Loc loc, boolean eager,
      IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    if (eager) {
      return callEagerJob(evalT, func, args, loc, scope, vars);
    } else {
      var funcT = (FuncTB) func.type();
      var actualResT = typing.mapVarsLower(funcT.res(), vars);
      return new LazyJob(evalT, loc,
          () -> callEagerJob(evalT, actualResT, func, args, loc, scope, vars));
    }
  }

  public Job callEagerJob(TypeB evalT, Job func, ImmutableList<Job> args, Loc loc,
      IndexedScope<Job> scope) {
    var vars = inferVarsInCall(func, map(args, Job::type));
    return callEagerJob(evalT, func, args, loc, scope, vars);
  }

  private Job callEagerJob(TypeB evalT, Job func, ImmutableList<Job> args, Loc loc,
      IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var funcT = (FuncTB) func.type();
    var actualResT = typing.mapVarsLower(funcT.res(), vars);
    return callEagerJob(evalT, actualResT, func, args, loc, scope, vars);
  }

  private Job callEagerJob(TypeB evalT, TypeB actualResT, Job func, ImmutableList<Job> args,
      Loc loc, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var callJ = new CallJob(actualResT, func, args, loc, vars, scope, JobCreator.this);
    return convertIfNeeded(evalT, callJ);
  }

  private VarBounds<TypeB> inferVarsInCall(Job func, ImmutableList<TypeB> argTs) {
    var funcT = (CallableTB) func.type();
    return typing.inferVarBoundsLower(funcT.params(), argTs);
  }

  // Combine

  private Job combineLazy(CombineB combine, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nals.get(combine);
    var loc = nal.loc();
    return new LazyJob(combine.type(), loc,
        () -> combineEager(scope, vars, combine, nal));
  }

  private Job combineEager(CombineB combine, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nals.get(combine);
    return combineEager(scope, vars, combine, nal);
  }

  private Job combineEager(
      IndexedScope<Job> scope, VarBounds<TypeB> vars, CombineB combine, Nal nal) {
    var itemJs = eagerJobsFor(scope, vars, combine.items());
    var info = new TaskInfo(COMBINE, nal);
    var convertedItemJs = zip(combine.type().items(), itemJs, this::convertIfNeeded);
    var algorithm = new CombineAlgorithm(combine.type());
    return new Task(convertedItemJs, info, algorithm);
  }

  // If

  private Job ifLazy(IfB if_, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    Nal nal = nals.get(if_);
    return new LazyJob(if_.type(), nal.loc(),
        () -> ifEager(if_, nal, scope, vars));
  }

  private Job ifEager(IfB if_, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    Nal nal = nals.get(if_);
    return ifEager(if_, nal, scope, vars);
  }

  private Job ifEager(IfB if_, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var ifData = if_.data();
    var conditionJ = eagerJobFor(scope, vars, ifData.condition());
    var thenJ = lazyJobFor(scope, vars, ifData.then());
    var elseJ = lazyJobFor(scope, vars, ifData.else_());
    return new IfJob(if_.type(), conditionJ, thenJ, elseJ, nal.loc());
  }

  // Invoke

  private Job invokeLazy(InvokeB invoke, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    Nal nal = nals.get(invoke);
    return new LazyJob(invoke.type(), nal.loc(),
        () -> invokeEager(invoke, nal, scope, vars));
  }

  private Job invokeEager(InvokeB invoke, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    Nal nal = nals.get(invoke);
    return invokeEager(invoke, nal, scope, vars);
  }

  private Task invokeEager(
      InvokeB invoke, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var name = nal.name();
    var actualType = typing.mapVarsLower(invoke.type(), vars);
    var invokeData = invoke.data();
    var algorithm = new InvokeAlgorithm(actualType, name, invokeData.method(), methodLoader);
    var info = new TaskInfo(INTERNAL, name, nal.loc());
    var argsJ = eagerJobsFor(scope, vars, invokeData.args().items());
    return new Task(argsJ, info, algorithm);
  }

  // Map

  private Job mapLazy(MapB map, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    Nal nal = nals.get(map);
    return new LazyJob(map.type(), nal.loc(),
        () -> mapEager(map, nal, scope, vars));
  }

  private Job mapEager(MapB map, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    Nal nal = nals.get(map);
    return mapEager(map, nal, scope, vars);
  }

  private Job mapEager(MapB mapB, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    MapB.Data data = mapB.data();
    var arrayJ = eagerJobFor(scope, vars, data.array());
    var funcJ = eagerJobFor(scope, vars, data.func());
    TypeB actualType = typing.mapVarsLower(mapB.type(), vars);
    return new MapJob(actualType, arrayJ, funcJ, nal.loc(), scope, this);
  }

  // Order

  private Job orderLazy(OrderB order, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nals.get(order);
    return new LazyJob(order.type(), nal.loc(),
        () -> orderEager(order, scope, vars));
  }

  private Job orderEager(OrderB order, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nals.get(order);
    return orderEager(order, nal, scope, vars);
  }

  private Task orderEager(OrderB order, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var type = order.type();
    var actualT = (ArrayTB) typing.mapVarsLower(type, vars);
    var elemsJ = map(order.elems(), e -> eagerJobFor(scope, vars, e));
    var info = new TaskInfo(LITERAL, nal);
    return orderEager(actualT, elemsJ, info);
  }

  public Task orderEager(ArrayTB arrayTB, ImmutableList<Job> elemJs, TaskInfo info) {
    var convertedElemJs = convertIfNeeded(arrayTB.elem(), elemJs);
    var algorithm = new OrderAlgorithm(arrayTB);
    return new Task(convertedElemJs, info, algorithm);
  }

  // ParamRef

  private Job paramRefLazy(ParamRefB paramRef, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    return scope.get(paramRef.value().intValue());
  }

  // Select

  private Job selectLazy(SelectB select, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nals.get(select);
    return new LazyJob(select.type(), nal.loc(),
        () -> selectEager(select, nal, scope, vars));
  }

  private Job selectEager(SelectB select, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    return selectEager(select, nals.get(select), scope, vars);
  }

  private Job selectEager(
      SelectB select, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var data = select.data();
    var selectableJ = eagerJobFor(scope, vars, data.selectable());
    var indexJ = eagerJobFor(data.index());
    var actualEvalT = typing.mapVarsLower(select.type(), vars);
    var algorithmT = ((TupleTB) selectableJ.type()).items().get(data.index().toJ().intValue());
    var algorithm = new SelectAlgorithm(algorithmT);
    var info = new TaskInfo(SELECT, nal);
    var task = new Task(list(selectableJ, indexJ), info, algorithm);
    return convertIfNeeded(actualEvalT, task);
  }

  // Value

  private Job valueLazy(ValB val, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nals.get(val);
    var loc = nal.loc();
    return new LazyJob(val.cat(), loc, () -> new ValJob(val, nal));
  }

  private Job valueEager(ValB val, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nals.get(val);
    return new ValJob(val, nal);
  }

  // helper methods

  public Job callFuncEagerJob(TypeB actualResT, FuncB func, ImmutableList<Job> args,
      Loc loc, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var job = eagerJobFor(new IndexedScope<>(scope, args), vars, func.body());
    var name = nals.get(func).name();
    return new VirtualJob(job, new TaskInfo(CALL, name, loc));
  }

  private ImmutableList<Job> convertIfNeeded(TypeB type, ImmutableList<Job> elemJs) {
    return map(elemJs, j -> convertIfNeeded(type, j));
  }

  private Job convertIfNeeded(TypeB type, Job job) {
    if (job.type().equals(type)) {
      return job;
    } else {
      var algorithm = new ConvertAlgorithm(type, typing);
      return new Task(list(job), new TaskInfo(INTERNAL, job), algorithm);
    }
  }

  public record Handler<E>(
      TriFunction<E, IndexedScope<Job>, VarBounds<TypeB>, Job> lazyJob,
      TriFunction<E, IndexedScope<Job>, VarBounds<TypeB>, Job> eagerJob) {
    public TriFunction<E, IndexedScope<Job>, VarBounds<TypeB>, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}
