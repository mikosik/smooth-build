package org.smoothbuild.vm.job;

import static org.smoothbuild.lang.base.type.api.VarBounds.varBounds;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.vm.job.job.JobKind.CALL;
import static org.smoothbuild.vm.job.job.JobKind.COMBINE;
import static org.smoothbuild.vm.job.job.JobKind.CONVERT;
import static org.smoothbuild.vm.job.job.JobKind.INVOKE;
import static org.smoothbuild.vm.job.job.JobKind.ORDER;
import static org.smoothbuild.vm.job.job.JobKind.SELECT;

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
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;
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
import org.smoothbuild.vm.job.job.ConstJob;
import org.smoothbuild.vm.job.job.IfJob;
import org.smoothbuild.vm.job.job.Job;
import org.smoothbuild.vm.job.job.JobInfo;
import org.smoothbuild.vm.job.job.LazyJob;
import org.smoothbuild.vm.job.job.MapJob;
import org.smoothbuild.vm.job.job.Task;
import org.smoothbuild.vm.job.job.VirtualJob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JobCreator {
  private static final String PARENTHESES = "()";
  private static final String PARENTHESES_INVOKE = "()~";
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
    return eagerJobFor(new IndexedScope<>(list()), varBounds(), obj);
  }

  private ImmutableList<Job> eagerJobsFor(
      ImmutableList<? extends ObjB> objs, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
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
    var loc = locFor(call);
    var newVars = inferVarsInCall(funcJ, map(argsJ, Job::type));
    var actualEvalT = typing.mapVarsLower(call.type(), vars);
    return callJob(actualEvalT, funcJ, argsJ, loc, eager, scope, newVars);
  }

  private Job callJob(TypeB actualEvalT, Job func, ImmutableList<Job> args, Loc loc, boolean eager,
      IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    if (eager) {
      return callEagerJob(actualEvalT, func, args, loc, scope, vars);
    } else {
      var funcT = (FuncTB) func.type();
      var actualResT = typing.mapVarsLower(funcT.res(), vars);
      return new LazyJob(actualEvalT, loc,
          () -> callEagerJob(actualEvalT, actualResT, func, args, loc, scope, vars));
    }
  }

  public Job callEagerJob(TypeB actualEvalT, Job func, ImmutableList<Job> args, Loc loc,
      IndexedScope<Job> scope) {
    var vars = inferVarsInCall(func, map(args, Job::type));
    return callEagerJob(actualEvalT, func, args, loc, scope, vars);
  }

  private Job callEagerJob(TypeB actualEvalT, Job func, ImmutableList<Job> args, Loc loc,
      IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var funcT = (FuncTB) func.type();
    var actualResT = typing.mapVarsLower(funcT.res(), vars);
    return callEagerJob(actualEvalT, actualResT, func, args, loc, scope, vars);
  }

  private Job callEagerJob(TypeB actualEvalT, TypeB actualResT, Job func, ImmutableList<Job> args,
      Loc loc, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var callJ = new CallJob(actualResT, func, args, loc, vars, scope, JobCreator.this);
    return convertIfNeeded(actualEvalT, loc, callJ);
  }

  private VarBounds<TypeB> inferVarsInCall(Job func, ImmutableList<TypeB> argTs) {
    var funcT = (CallableTB) func.type();
    return inferVarsInCallLike(funcT, argTs);
  }

  private VarBounds<TypeB> inferVarsInCallLike(CallableTB callableT, ImmutableList<TypeB> argTs) {
    return typing.inferVarBoundsLower(callableT.params(), argTs);
  }

  // Combine

  private Job combineLazy(CombineB combine, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(combine);
    var loc = nal.loc();
    return new LazyJob(combine.type(), loc,
        () -> combineEager(scope, vars, combine, nal));
  }

  private Job combineEager(CombineB combine, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(combine);
    return combineEager(scope, vars, combine, nal);
  }

  private Job combineEager(
      IndexedScope<Job> scope, VarBounds<TypeB> vars, CombineB combine, Nal nal) {
    var actualEvalT = (TupleTB) typing.mapVarsLower(combine.type(), vars);
    var itemJs = eagerJobsFor(combine.items(), scope, vars);
    var convertedItemJs = convertJobs(actualEvalT.items(), nal, itemJs);
    var info = new JobInfo(COMBINE, nal);
    var algorithm = new CombineAlgorithm(actualEvalT);
    return new Task(algorithm, convertedItemJs, info);
  }

  // If

  private Job ifLazy(IfB if_, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(if_);
    return new LazyJob(if_.type(), nal.loc(),
        () -> ifEager(if_, nal, scope, vars));
  }

  private Job ifEager(IfB if_, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(if_);
    return ifEager(if_, nal, scope, vars);
  }

  private Job ifEager(IfB if_, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var ifData = if_.data();
    var conditionJ = eagerJobFor(scope, vars, ifData.condition());
    var actualEvalT = typing.mapVarsLower(if_.type(), vars);
    var thenJ = clauseJob(actualEvalT, nal, ifData.then(), scope, vars);
    var elseJ = clauseJob(actualEvalT, nal, ifData.else_(), scope, vars);
    return new IfJob(actualEvalT, conditionJ, thenJ, elseJ, nal.loc());
  }

  private Job clauseJob(TypeB actualEvalT, Nal nal, ObjB clause, IndexedScope<Job> scope,
      VarBounds<TypeB> vars) {
    return convertIfNeeded(actualEvalT, nal.loc(), lazyJobFor(scope, vars, clause));
  }

  // Invoke

  private Job invokeLazy(InvokeB invoke, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(invoke);
    return new LazyJob(invoke.type(), nal.loc(),
        () -> invokeEager(invoke, nal, scope, vars));
  }

  private Job invokeEager(InvokeB invoke, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(invoke);
    return invokeEager(invoke, nal, scope, vars);
  }

  private Job invokeEager(
      InvokeB invoke, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var name = nal.name();
    var invokeData = invoke.data();
    var methodT = invokeData.method().type();
    var argJs = eagerJobsFor(invokeData.args().items(), scope, vars);
    var newVars = inferVarsInCallLike(methodT, map(argJs, Job::type));
    var actualResT = typing.mapVarsLower(methodT.res(), newVars);
    var algorithm = new InvokeAlgorithm(actualResT, name, invokeData.method(), methodLoader);
    var info = new JobInfo(INVOKE, name + PARENTHESES_INVOKE, nal.loc());
    var actualArgTs = map(methodT.params(), t -> typing.mapVarsLower(t, newVars));
    var convertedArgJs = convertJobs(actualArgTs, nal, argJs);
    var task = new Task(algorithm, convertedArgJs, info);
    var actualEvalT = typing.mapVarsLower(invoke.type(), vars);
    return convertIfNeeded(actualEvalT, nal.loc(), task);
  }

  // Map

  private Job mapLazy(MapB map, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(map);
    return new LazyJob(map.type(), nal.loc(),
        () -> mapEager(map, nal, scope, vars));
  }

  private Job mapEager(MapB map, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(map);
    return mapEager(map, nal, scope, vars);
  }

  private Job mapEager(MapB mapB, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    MapB.Data data = mapB.data();
    var arrayJ = eagerJobFor(scope, vars, data.array());
    var funcJ = eagerJobFor(scope, vars, data.func());
    var actualType = typing.mapVarsLower(mapB.type(), vars);
    return new MapJob(actualType, arrayJ, funcJ, nal.loc(), scope, this);
  }

  // Order

  private Job orderLazy(OrderB order, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(order);
    return new LazyJob(order.type(), nal.loc(),
        () -> orderEager(order, nal, scope, vars));
  }

  private Job orderEager(OrderB order, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(order);
    return orderEager(order, nal, scope, vars);
  }

  private Task orderEager(OrderB order, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var type = order.type();
    var actualEvalT = (ArrayTB) typing.mapVarsLower(type, vars);
    var elemJs = map(order.elems(), e -> eagerJobFor(scope, vars, e));
    var info = new JobInfo(ORDER, nal);
    return orderEager(actualEvalT, elemJs, info);
  }

  public Task orderEager(ArrayTB arrayTB, ImmutableList<Job> elemJs, JobInfo info) {
    var convertedElemJs = convertIfNeeded(arrayTB.elem(), info.loc(), elemJs);
    var algorithm = new OrderAlgorithm(arrayTB);
    return new Task(algorithm, convertedElemJs, info);
  }

  // ParamRef

  private Job paramRefLazy(ParamRefB paramRef, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    return scope.get(paramRef.value().intValue());
  }

  // Select

  private Job selectLazy(SelectB select, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(select);
    return new LazyJob(select.type(), nal.loc(),
        () -> selectEager(select, nal, scope, vars));
  }

  private Job selectEager(SelectB select, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(select);
    return selectEager(select, nal, scope, vars);
  }

  private Job selectEager(
      SelectB select, Nal nal, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var data = select.data();
    var selectableJ = eagerJobFor(scope, vars, data.selectable());
    var indexJ = eagerJobFor(scope, vars, data.index());
    var actualEvalT = typing.mapVarsLower(select.type(), vars);
    var algorithmT = ((TupleTB) selectableJ.type()).items().get(data.index().toJ().intValue());
    var algorithm = new SelectAlgorithm(algorithmT);
    var info = new JobInfo(SELECT, nal);
    var task = new Task(algorithm, list(selectableJ, indexJ), info);
    return convertIfNeeded(actualEvalT, nal.loc(), task);
  }

  // Value

  private Job constLazy(ValB val, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(val);
    var loc = nal.loc();
    return new LazyJob(val.cat(), loc, () -> new ConstJob(val, nal));
  }

  private Job constEager(ValB val, IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var nal = nalFor(val);
    return new ConstJob(val, nal);
  }

  // helper methods

  public Job callFuncEagerJob(TypeB actualEvalT, FuncB func, ImmutableList<Job> args, Loc loc,
      IndexedScope<Job> scope, VarBounds<TypeB> vars) {
    var job = eagerJobFor(new IndexedScope<>(scope, args), vars, func.body());
    var nal = nalFor(func);
    var name = nal.name() + PARENTHESES;
    var convertedJ = convertIfNeeded(actualEvalT, nal.loc(), job);
    return new VirtualJob(convertedJ, new JobInfo(CALL, name, loc));
  }

  private ImmutableList<Job> convertJobs(
      ImmutableList<TypeB> types, Nal nal, ImmutableList<Job> jobs) {
    return zip(types, jobs, (t, j) -> convertIfNeeded(t, nal.loc(), j));
  }

  private ImmutableList<Job> convertIfNeeded(TypeB type, Loc loc, ImmutableList<Job> elemJs) {
    return map(elemJs, j -> convertIfNeeded(type, loc, j));
  }

  private Job convertIfNeeded(TypeB type, Loc loc, Job job) {
    if (job.type().equals(type)) {
      return job;
    } else {
      var algorithm = new ConvertAlgorithm(type, typing);
      var info = new JobInfo(CONVERT, type.name() + " <- " + job.type().name(), loc);
      return new Task(algorithm, list(job), info);
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
      TriFunction<E, IndexedScope<Job>, VarBounds<TypeB>, Job> lazyJob,
      TriFunction<E, IndexedScope<Job>, VarBounds<TypeB>, Job> eagerJob) {
    public TriFunction<E, IndexedScope<Job>, VarBounds<TypeB>, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}
