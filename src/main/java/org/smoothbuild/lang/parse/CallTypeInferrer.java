package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class CallTypeInferrer {
  private final TypeFactoryS factory;
  private final TypingS typing;

  public CallTypeInferrer(TypeFactoryS factory, TypingS typing) {
    this.factory = factory;
    this.typing = typing;
  }

  public Maybe<TypeS> inferCallT(CallN call, TypeS resT, NList<ItemSigS> params) {
    var logBuffer = new LogBuffer();
    List<Optional<ArgNode>> assignedArgs = call.assignedArgs();
    findIllegalTypeAssignmentErrors(call, assignedArgs, params, logBuffer);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    List<Optional<TypeS>> assignedTs = assignedTs(params, assignedArgs);
    if (allAssignedTypesAreInferred(assignedTs)) {
      var boundedVars = typing.inferVarBoundsLower(
          map(params, ItemSigS::type),
          map(assignedTs, Optional::get));
      var varProblems = findVarProblems(call, boundedVars);
      if (!varProblems.isEmpty()) {
        logBuffer.logAll(varProblems);
        return maybeLogs(logBuffer);
      }
      TypeS mapped = typing.mapVars(resT, boundedVars, factory.lower());
      return maybeValueAndLogs(mapped, logBuffer);
    }
    return maybeLogs(logBuffer);
  }

  private void findIllegalTypeAssignmentErrors(CallN call,
      List<Optional<ArgNode>> assignedList, List<ItemSigS> params, Logger logger) {
    range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isPresent())
        .filter(i -> !isAssignable(params.get(i), assignedList.get(i).get()))
        .mapToObj(i -> illegalAssignmentError(call, params.get(i), assignedList.get(i).get()))
        .forEach(logger::log);
  }

  private boolean isAssignable(ItemSigS param, ArgNode arg) {
    return typing.isParamAssignable(param.type(), arg.type().get());
  }

  private static Log illegalAssignmentError(CallN call, ItemSigS param, ArgNode arg) {
    return parseError(arg.loc(), inCallToPrefix(call)
        + "Cannot assign argument of type " + arg.type().get().q() + " to parameter "
        + param.q() + " of type " + param.type().q() + ".");
  }

  private static String inCallToPrefix(CallN call) {
    return "In call to function with type " + call.callable().type().get().q() + ": ";
  }

  private List<Optional<TypeS>> assignedTs(List<ItemSigS> params, List<Optional<ArgNode>> args) {
    List<Optional<TypeS>> assigned = new ArrayList<>();
    for (int i = 0; i < params.size(); i++) {
      Optional<ArgNode> arg = args.get(i);
      if (arg.isPresent()) {
        assigned.add(arg.get().type());
      } else {
        assigned.add(params.get(i).defaultValT());
      }
    }
    return assigned;
  }

  private static boolean allAssignedTypesAreInferred(List<Optional<TypeS>> assigned) {
    return assigned.stream().allMatch(Optional::isPresent);
  }

  private ImmutableList<Log> findVarProblems(CallN call, BoundsMap<TypeS> boundedVars) {
    return boundedVars.map().values().stream()
        .filter(b -> typing.contains(b.bounds().lower(), factory.any()))
        .map(b -> parseError(call, "Cannot infer actual type for type var "
            + b.var().q() + "."))
        .collect(toImmutableList());
  }
}
