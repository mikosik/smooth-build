package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.like.Param;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.lang.type.TypingS;
import org.smoothbuild.lang.type.VarBoundsS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgN;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class CallTypeInferrer {
  private final TypeSF typeSF;
  private final TypingS typing;

  public CallTypeInferrer(TypeSF typeSF, TypingS typing) {
    this.typeSF = typeSF;
    this.typing = typing;
  }

  public Maybe<TypeS> inferCallT(CallN call, TypeS resT, NList<Param> params) {
    var logBuffer = new LogBuffer();
    var args = call.assignedArgs();
    findIllegalTypeAssignmentErrors(args, params.map(Param::sig), logBuffer);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    var assignedTs = map(args, arg -> arg.type().get());
    var boundedVars = typing.inferVarBoundsLower(map(params, Param::type), assignedTs);
    var varProblems = findVarProblems(call, boundedVars);
    if (!varProblems.isEmpty()) {
      logBuffer.logAll(varProblems);
      return maybeLogs(logBuffer);
    }
    TypeS mapped = typing.mapVarsLower(resT, boundedVars);
    return maybeValueAndLogs(mapped, logBuffer);
  }

  private void findIllegalTypeAssignmentErrors(
      ImmutableList<ArgN> args, List<ItemSigS> params, Logger logger) {
    range(0, args.size())
        .filter(i -> !isAssignable(params.get(i), args.get(i)))
        .mapToObj(i -> illegalAssignmentError(params, params.get(i), args.get(i)))
        .forEach(logger::log);
  }

  private boolean isAssignable(ItemSigS param, ArgN arg) {
    return typing.isParamAssignable(param.type(), arg.type().get());
  }

  private static Log illegalAssignmentError(List<ItemSigS> params, ItemSigS param, ArgN arg) {
    return parseError(arg.loc(), messagePrefix(params)
        + "Cannot assign argument of type " + arg.type().get().q() + " to parameter "
        + param.q() + " of type " + param.type().q() + ".");
  }

  private static String messagePrefix(List<ItemSigS> params) {
    var paramsString = params.stream()
        .map(ItemSigS::typeAndName)
        .collect(joining(", "));
    return "In call to function with parameters (" + paramsString + "): ";
  }

  private ImmutableList<Log> findVarProblems(CallN call, VarBoundsS varBounds) {
    return varBounds.map().values().stream()
        .filter(b -> typing.contains(b.bounds().lower(), typeSF.any()))
        .map(b -> parseError(call, "Cannot infer actual type for type var "
            + b.var().q() + "."))
        .collect(toImmutableList());
  }
}
