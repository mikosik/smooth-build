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
import org.smoothbuild.lang.base.define.ItemSignature;
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

  public Maybe<TypeS> inferCallType(CallN call, TypeS resultType,
      NList<ItemSignature> params) {
    var logBuffer = new LogBuffer();
    List<Optional<ArgNode>> assignedArgs = call.assignedArgs();
    findIllegalTypeAssignmentErrors(call, assignedArgs, params, logBuffer);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    List<Optional<TypeS>> assignedTypes = assignedTypes(params, assignedArgs);
    if (allAssignedTypesAreInferred(assignedTypes)) {
      var boundedVariables = typing.inferVariableBoundsInCall(
          map(params, ItemSignature::type),
          map(assignedTypes, Optional::get));
      var variableProblems = findVariableProblems(call, boundedVariables);
      if (!variableProblems.isEmpty()) {
        logBuffer.logAll(variableProblems);
        return maybeLogs(logBuffer);
      }
      TypeS mapped = typing.mapVariables(resultType, boundedVariables, factory.lower());
      return maybeValueAndLogs(mapped, logBuffer);
    }
    return maybeLogs(logBuffer);
  }

  private void findIllegalTypeAssignmentErrors(CallN call,
      List<Optional<ArgNode>> assignedList, List<ItemSignature> params, Logger logger) {
    range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isPresent())
        .filter(i -> !isAssignable(params.get(i), assignedList.get(i).get()))
        .mapToObj(i -> illegalAssignmentError(call, params.get(i), assignedList.get(i).get()))
        .forEach(logger::log);
  }

  private boolean isAssignable(ItemSignature param, ArgNode arg) {
    return typing.isParamAssignable(param.type(), arg.type().get());
  }

  private static Log illegalAssignmentError(CallN call, ItemSignature param, ArgNode arg) {
    return parseError(arg.location(), inCallToPrefix(call)
        + "Cannot assign argument of type " + arg.type().get().q() + " to parameter "
        + param.q() + " of type " + param.type().q() + ".");
  }

  private static String inCallToPrefix(CallN call) {
    return "In call to function with type " + call.func().type().get().q() + ": ";
  }

  private List<Optional<TypeS>> assignedTypes(
      List<ItemSignature> params, List<Optional<ArgNode>> args) {
    List<Optional<TypeS>> assigned = new ArrayList<>();
    for (int i = 0; i < params.size(); i++) {
      Optional<ArgNode> arg = args.get(i);
      if (arg.isPresent()) {
        assigned.add(arg.get().type());
      } else {
        assigned.add(params.get(i).defaultValType());
      }
    }
    return assigned;
  }

  private static boolean allAssignedTypesAreInferred(List<Optional<TypeS>> assigned) {
    return assigned.stream().allMatch(Optional::isPresent);
  }

  private ImmutableList<Log> findVariableProblems(
      CallN call, BoundsMap<TypeS> boundedVariables) {
    return boundedVariables.map().values().stream()
        .filter(b -> typing.contains(b.bounds().lower(), factory.any()))
        .map(b -> parseError(call, "Cannot infer actual type for type variable "
            + b.variable().q() + "."))
        .collect(toImmutableList());
  }
}
