package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.TypeS.prefixFreeVarsWithIndex;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.parse.ConstructExplicitArgs.constructExplicitArgs;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.define.MonoObjS;
import org.smoothbuild.lang.define.RefableS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.lang.type.solver.ConstrDecomposeExc;
import org.smoothbuild.lang.type.solver.ConstrSolver;
import org.smoothbuild.lang.type.solver.Denormalizer;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgP;
import org.smoothbuild.parse.ast.CallP;
import org.smoothbuild.parse.ast.ObjP;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.util.bindings.OptionalBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class CallTypeInferrer {
  private final CallP call;
  private final OptionalBindings<RefableS> nameBindings;
  private final LogBuffer logBuffer;

  public CallTypeInferrer(CallP call, OptionalBindings<RefableS> nameBindings, LogBuffer logBuffer) {
    this.call = call;
    this.nameBindings = nameBindings;
    this.logBuffer = logBuffer;
  }

  public Optional<MonoTS> inferCallT() {
    ObjP callee = call.callee();

    if (callee.typeS().isEmpty()) {
      return empty();
    }

    if (!(callee.typeS().get() instanceof FuncTS calleeT)) {
      logBuffer.log(parseError(call, saneName(callee)
          + " cannot be called as it is not a function but " + callee.typeS().get().q() + "."));
      return empty();
    }

    var funcParams = calleeParams(callee, calleeT);
    if (funcParams.isEmpty()) {
      return empty();
    }

    var params = funcParams.get();
    Maybe<ImmutableList<Optional<ArgP>>> args = constructExplicitArgs(call, params);
    if (args.containsProblem()) {
      logBuffer.logAll(args.logs());
      return empty();
    } else if (someArgHasNotInferredType(args.value())) {
      return empty();
    } else {
      call.setExplicitArgs(args.value());
      return inferCallT(params, calleeT, args.value());
    }
  }

  private Optional<NList<Param>> calleeParams(ObjP callee, FuncTS calleeT) {
    if (callee instanceof RefP refP) {
      return nameBindings.get(refP.name()).value()
          .map(r -> calleeParams(r, calleeT));
    } else {
      return Optional.of(calleeParams(calleeT));
    }
  }

  private static NList<Param> calleeParams(RefableS refableS, FuncTS calleeT) {
    if (refableS instanceof FuncS funcS) {
      return funcS.params().map(p -> new Param(p.sig(), p.body().map(MonoObjS::type)));
    } else {
      return calleeParams(calleeT);
    }
  }

  private static NList<Param> calleeParams(FuncTS calleeT) {
    return nlist(map(calleeT.params(), p -> new Param(itemSigS(p), empty())));
  }

  private static boolean someArgHasNotInferredType(ImmutableList<Optional<ArgP>> args) {
    return args.stream()
        .filter(Optional::isPresent)
        .anyMatch(a -> a.get().typeS().isEmpty());
  }

  private static String saneName(ObjP node) {
    if (node instanceof RefP refP) {
      return "`" + refP.name() + "`";
    }
    return "expression";
  }

  private Optional<MonoTS> inferCallT(NList<Param> params, FuncTS calleeT,
      ImmutableList<Optional<ArgP>> explicitArgs) {
    var argTs = explicitAndDefaultArgTs(params, explicitArgs);
    var prefixedArgTs = prefixFreeVarsWithIndex(argTs);
    var prefixedCalleeT = (FuncTS) calleeT.mapFreeVars(v -> v.prefixed("callee"));
    var prefixedParamTs = prefixedCalleeT.params();
    var solver = new ConstrSolver();
    for (int i = 0; i < argTs.size(); i++) {
      try {
        solver.addConstr(constrS(prefixedArgTs.get(i), prefixedParamTs.get(i)));
      } catch (ConstrDecomposeExc e) {
        NList<ItemSigS> paramSigs = params.map(Param::sig);
        logBuffer.log(illegalAssignmentError(argTs, i, paramSigs));
        return empty();
      }
    }

    var constrGraph = solver.graph();
    var denormalizer = new Denormalizer(constrGraph);
    for (var prefixedParamT : prefixedParamTs) {
      var typeS = denormalize(denormalizer, prefixedParamT);
      if (typeS.includes(TypeFS.any())) {
        logBuffer.log(paramInferringError(call.loc(), prefixedParamT));
        return empty();
      }
    }
    for (int i = 0; i < argTs.size(); i++) {
      int index = i;
      explicitArgs.get(i)
          .ifPresent(a -> storeActualTypeIfNeeded(a.obj(), prefixedArgTs.get(index), denormalizer));
    }
    var prefixedResT = prefixedCalleeT.res();
    var actualResT = denormalize(denormalizer, prefixedResT);
    if (actualResT.includes(TypeFS.any())) {
      logBuffer.log(resInferringError(calleeT.res()));
      return empty();
    }

    storeActualTypeIfNeeded(call.callee(), TypeFS.func(prefixedResT, prefixedArgTs), denormalizer);
    return Optional.of(actualResT);
  }

  private ImmutableList<TypeS> explicitAndDefaultArgTs(NList<Param> params,
      ImmutableList<Optional<ArgP>> explicitArgs) {
    return IntStream.range(0, params.size())
        .mapToObj(i -> explicitOrDefaultArgT(i, explicitArgs, params))
        .collect(toImmutableList());
  }

  private static TypeS explicitOrDefaultArgT(
      int i, ImmutableList<Optional<ArgP>> explicitArgs, NList<Param> params) {
    return explicitArgs.get(i)
        .map(argP -> (TypeS) argP.obj().typeS().get())
        .orElseGet(() -> params.get(i).bodyT().get());
  }

  private void storeActualTypeIfNeeded(ObjP obj, MonoTS monoTS, Denormalizer denormalizer) {
    if (obj instanceof RefP refP) {
      Optional<? extends RefableS> refableS = nameBindings.get(refP.name()).value();
      if (refableS.isPresent() && refableS.get().type() instanceof PolyTS) {
        refP.setInferredMonoType(denormalize(denormalizer, monoTS));
      }
    }
  }

  private static MonoTS denormalize(Denormalizer denormalizer, MonoTS typeS) {
    return denormalizer.denormalizeVars(typeS, LOWER);
  }

  private static Log paramInferringError(Loc loc, MonoTS paramT) {
    return error(loc + ": Cannot infer actual type for "
        + paramT.mapVars(VarS::unprefixed).q() + ".");
  }

  private Log resInferringError(MonoTS resT) {
    return error(call.loc() + ": Cannot infer call actual result type " + resT.q() + ".");
  }

  private Log illegalAssignmentError(
      ImmutableList<TypeS> argTs, int i, NList<ItemSigS> paramSigs) {
    var paramSig = paramSigs.get(i);
    var loc = call.args().get(i).loc();
    var argT = argTs.get(i);
    return parseError(loc, messagePrefix(paramSigs)
        + "Cannot assign argument of type " + argT.q() + " to parameter "
        + paramSig.q() + " of type " + paramSig.type().q() + ".");
  }

  private static String messagePrefix(List<ItemSigS> params) {
    var paramsString = toCommaSeparatedString(params, ItemSigS::typeAndName);
    return "In call to function with parameters (" + paramsString + "): ";
  }
}
