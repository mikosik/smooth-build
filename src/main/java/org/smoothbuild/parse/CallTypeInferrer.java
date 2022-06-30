package org.smoothbuild.parse;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.TypeS.prefixFreeVarsWithIndex;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.parse.ConstructArgList.constructArgList;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.like.common.FuncC;
import org.smoothbuild.lang.like.common.ObjC;
import org.smoothbuild.lang.like.common.ParamC;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.TypeFS;
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
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class CallTypeInferrer {
  public Optional<MonoTS> inferCallT(CallP call, LogBuffer logBuffer) {
    ObjP callee = call.callee();

    if (callee.typeS().isEmpty()) {
      return empty();
    }

    if (!(callee.typeS().get() instanceof FuncTS calleeT)) {
      logBuffer.log(parseError(call, saneName(callee)
          + " cannot be called as it is not a function but " + callee.typeS().get().q() + "."));
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

  public static Optional<NList<ParamC>> funcParams(ObjP callee, FuncTS calleeT) {
    if (callee instanceof RefP refP) {
      if (refP.referenced() instanceof FuncC funcC) {
        return funcC.paramsC();
      }
    }
    return Optional.of(nList(map(calleeT.params(), p -> new ParamC(itemSigS(p), empty()))));
  }

  private static boolean someArgHasNotInferredType(ImmutableList<ArgP> args) {
    return args.stream()
        .anyMatch(a -> a.typeS().isEmpty());
  }

  private static String saneName(ObjP node) {
    if (node instanceof RefP refP) {
      return "`" + refP.name() + "`";
    }
    return "expression";
  }

  public Optional<MonoTS> inferCallT(CallP call, NList<ParamC> paramCs, FuncTS calleeT,
      LogBuffer logBuffer) {
    var args = call.assignedArgs();
    var prefixedArgTs = prefixFreeVarsWithIndex(map(args, a -> a.obj().typeS().get()));
    var prefixedCalleeT = (FuncTS) calleeT.mapFreeVars(v -> v.prefixed("callee"));
    var prefixedParamTs = prefixedCalleeT.params();
    var solver = new ConstrSolver();
    for (int i = 0; i < args.size(); i++) {
      try {
        solver.addConstr(constrS(prefixedArgTs.get(i), prefixedParamTs.get(i)));
      } catch (ConstrDecomposeExc e) {
        NList<ItemSigS> paramSigs = paramCs.map(ParamC::sig);
        logBuffer.log(illegalAssignmentError(paramSigs, paramSigs.get(i), args.get(i)));
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
    for (int i = 0; i < args.size(); i++) {
      storeActualTypeIfNeeded(args.get(i).obj(), prefixedArgTs.get(i), denormalizer);
    }
    var prefixedResT = prefixedCalleeT.res();
    var actualResT = denormalize(denormalizer, prefixedResT);
    if (actualResT.includes(TypeFS.any())) {
      logBuffer.log(resInferringError(call, calleeT.res()));
      return empty();
    }

    storeActualTypeIfNeeded(call.callee(), TypeFS.func(prefixedResT, prefixedArgTs), denormalizer);
    return Optional.of(actualResT);
  }

  private void storeActualTypeIfNeeded(ObjC objC, MonoTS monoTS, Denormalizer denormalizer) {
    if (objC instanceof RefP refP && refP.referenced().typeS().get() instanceof PolyTS) {
      refP.setInferredMonoType(denormalize(denormalizer, monoTS));
    }
  }

  private MonoTS denormalize(Denormalizer denormalizer, MonoTS typeS) {
    return denormalizer.denormalizeVars(typeS, LOWER);
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
        + "Cannot assign argument of type " + arg.typeS().get().q() + " to parameter "
        + param.q() + " of type " + param.type().q() + ".");
  }

  private static String messagePrefix(List<ItemSigS> params) {
    var paramsString = toCommaSeparatedString(params, ItemSigS::typeAndName);
    return "In call to function with parameters (" + paramsString + "): ";
  }
}
