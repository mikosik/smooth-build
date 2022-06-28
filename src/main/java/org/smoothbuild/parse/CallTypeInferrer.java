package org.smoothbuild.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.TypeS.prefixFreeVarsWithIndex;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.parse.ConstructArgList.constructArgList;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.NList.nList;
import static org.smoothbuild.util.collect.Optionals.pullUp;
import static org.smoothbuild.util.type.Side.LOWER;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.like.Param;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.TypingS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.lang.type.solver.ConstrDecomposeExc;
import org.smoothbuild.lang.type.solver.DenormalizerS;
import org.smoothbuild.lang.type.solver.SolverS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgP;
import org.smoothbuild.parse.ast.CallP;
import org.smoothbuild.parse.ast.FuncP;
import org.smoothbuild.parse.ast.ObjP;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class CallTypeInferrer {
  private final TypeFS typeFS;
  private final TypingS typing;

  public CallTypeInferrer(TypeFS typeFS, TypingS typing) {
    this.typeFS = typeFS;
    this.typing = typing;
  }

  public Optional<MonoTS> inferCallT(CallP call, LogBuffer logBuffer) {
    ObjP callee = call.callee();

    if (callee.typeO().isEmpty()) {
      return empty();
    }

    if (!(callee.typeO().get() instanceof FuncTS calleeT)) {
      logBuffer.log(parseError(call, description(callee)
          + " cannot be called as it is not a function but " + callee.typeO().get().q() + "."));
      return empty();
    }

    var funcParams = funcParams(callee, calleeT);
    if (funcParams.isEmpty()) {
      return empty();
    }

    var params = funcParams.get();
    Maybe<ImmutableList<ArgP>> args = constructArgList(call, params);
    if (args.containsProblem()) {
      logBuffer.logAll(args.logs());
      return empty();
    } else if (someArgHasNotInferredType(args.value())) {
      return empty();
    } else {
      call.setAssignedArgs(args.value());
      return inferCallT(call, params, calleeT, logBuffer);
    }
  }

  public static Optional<NList<Param>> funcParams(ObjP callee, FuncTS calleeT) {
    if (callee instanceof RefP refP) {
      Refable referenced = refP.referenced();
      if (referenced instanceof FuncS funcS) {
        return Optional.of(funcS.params().map(p -> new Param(p.sig(), p.body())));
      } else if (referenced instanceof FuncP funcP) {
        var params = map(
            funcP.params().list(), p -> p.sig().map(sig -> new Param(sig, p.body())));
        return pullUp(params).map(NList::nList);
      }
    }
    return Optional.of(nList(map(calleeT.params(), p -> new Param(itemSigS(p), empty()))));
  }

  private static boolean someArgHasNotInferredType(ImmutableList<ArgP> args) {
    return args.stream()
        .anyMatch(a -> a.typeO().isEmpty());
  }

  private static String description(ObjP node) {
    if (node instanceof RefP refP) {
      return "`" + refP.name() + "`";
    }
    return "expression";
  }

  public Optional<MonoTS> inferCallT(CallP call, NList<Param> params, FuncTS calleeT,
      LogBuffer logBuffer) {
    var args = call.assignedArgs();
    var prefixedArgTs = prefixFreeVarsWithIndex(map(args, a -> a.obj().typeO().get()));
    var prefixedCalleeT = (FuncTS) calleeT.mapFreeVars(v -> v.prefixed("callee"));
    var prefixedParamTs = prefixedCalleeT.params();
    var solver = new SolverS(typeFS);
    for (int i = 0; i < args.size(); i++) {
      try {
        solver.addConstr(constrS(prefixedArgTs.get(i), prefixedParamTs.get(i)));
      } catch (ConstrDecomposeExc e) {
        NList<ItemSigS> paramSigs = params.map(Param::sig);
        logBuffer.log(illegalAssignmentError(paramSigs, paramSigs.get(i), args.get(i)));
        return empty();
      }
    }

    var constrGraph = solver.graph();
    var denormalizer = new DenormalizerS(typeFS, constrGraph);
    for (var prefixedParamT : prefixedParamTs) {
      var typeS = denormalizeAndResolveMerges(denormalizer, prefixedParamT);
      if (typeS.includes(typeFS.any())) {
        logBuffer.log(paramInferringError(call.loc(), prefixedParamT));
        return empty();
      }
    }
    for (int i = 0; i < args.size(); i++) {
      storeActualTypeIfNeeded(args.get(i).obj(), prefixedArgTs.get(i), denormalizer);
    }
    var prefixedResT = prefixedCalleeT.res();
    var actualResT = denormalizeAndResolveMerges(denormalizer, prefixedResT);
    if (actualResT.includes(typeFS.any())) {
      logBuffer.log(resInferringError(call, calleeT.res()));
      return empty();
    }

    storeActualTypeIfNeeded(call.callee(), typeFS.func(prefixedResT, prefixedArgTs), denormalizer);
    return Optional.of(actualResT);
  }

  private void storeActualTypeIfNeeded(Obj obj, MonoTS monoTS, DenormalizerS denormalizer) {
    if (obj instanceof RefP refP && refP.referenced().typeO().get() instanceof PolyTS) {
      refP.setInferredMonoType(denormalizeAndResolveMerges(denormalizer, monoTS));
    }
  }

  private MonoTS denormalizeAndResolveMerges(DenormalizerS denormalizer, MonoTS typeS) {
    var denormalizedT = denormalizer.denormalizeVars(typeS, LOWER);
    return typing.resolveMerges(denormalizedT);
  }

  private static Log paramInferringError(Loc loc, MonoTS paramT) {
    return error(loc + ": Cannot infer actual type for "
        + paramT.mapVars(VarS::unprefixed).q() + ".");
  }

  private static Log resInferringError(CallP call, MonoTS resT) {
    return error(call.loc() + ": Cannot infer call actual result type " + resT.q() + ".");
  }

  private static Log illegalAssignmentError(List<ItemSigS> params, ItemSigS param, ArgP arg) {
    return parseError(arg.loc(), messagePrefix(params)
        + "Cannot assign argument of type " + arg.typeO().get().q() + " to parameter "
        + param.q() + " of type " + param.type().q() + ".");
  }

  private static String messagePrefix(List<ItemSigS> params) {
    var paramsString = toCommaSeparatedString(params, ItemSigS::typeAndName);
    return "In call to function with parameters (" + paramsString + "): ";
  }
}
