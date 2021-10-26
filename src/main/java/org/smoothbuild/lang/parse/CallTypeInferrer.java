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
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;

import com.google.common.collect.ImmutableList;

public class CallTypeInferrer {
  private final Typing typing;

  public CallTypeInferrer(Typing typing) {
    this.typing = typing;
  }

  public Maybe<Type> inferCallType(CallNode call, Type resultType,
      List<ItemSignature> parameters) {
    var logBuffer = new LogBuffer();
    List<Optional<ArgNode>> assignedArgs = call.assignedArgs();
    findIllegalTypeAssignmentErrors(call, assignedArgs, parameters, logBuffer);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    List<Optional<Type>> assignedTypes = assignedTypes(parameters, assignedArgs);
    if (allAssignedTypesAreInferred(assignedTypes)) {
      var boundedVariables = typing.inferVariableBoundsInCall(
          resultType,
          map(parameters, ItemSignature::type),
          map(assignedTypes, Optional::get));
      var variableProblems = findVariableProblems(call, boundedVariables);
      if (!variableProblems.isEmpty()) {
        logBuffer.logAll(variableProblems);
        return maybeLogs(logBuffer);
      }
      Type mapped = typing.mapVariables(resultType, boundedVariables, typing.lower());
      return maybeValueAndLogs(mapped, logBuffer);
    }
    return maybeLogs(logBuffer);
  }

  private void findIllegalTypeAssignmentErrors(CallNode call,
      List<Optional<ArgNode>> assignedList, List<ItemSignature> parameters, Logger logger) {
    range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isPresent())
        .filter(i -> !isAssignable(parameters.get(i), assignedList.get(i).get()))
        .mapToObj(i -> illegalAssignmentError(call, parameters.get(i), assignedList.get(i).get()))
        .forEach(logger::log);
  }

  private boolean isAssignable(ItemSignature parameter, ArgNode arg) {
    return typing.isParamAssignable(parameter.type(), arg.type().get());
  }

  private static Log illegalAssignmentError(CallNode call, ItemSignature parameter, ArgNode arg) {
    return parseError(arg.location(), inCallToPrefix(call)
        + "Cannot assign argument of type " + arg.type().get().q() + " to parameter "
        + parameter.q() + " of type " + parameter.type().q() + ".");
  }

  private static String inCallToPrefix(CallNode call) {
    return "In call to function with type " + call.function().type().get().q() + ": ";
  }

  private List<Optional<Type>> assignedTypes(
      List<ItemSignature> parameters, List<Optional<ArgNode>> arguments) {
    List<Optional<Type>> assigned = new ArrayList<>();
    for (int i = 0; i < parameters.size(); i++) {
      Optional<ArgNode> arg = arguments.get(i);
      if (arg.isPresent()) {
        assigned.add(arg.get().type());
      } else {
        assigned.add(parameters.get(i).defaultValueType());
      }
    }
    return assigned;
  }

  private static boolean allAssignedTypesAreInferred(List<Optional<Type>> assigned) {
    return assigned.stream().allMatch(Optional::isPresent);
  }

  private ImmutableList<Log> findVariableProblems(
      CallNode call, BoundsMap boundedVariables) {
    return boundedVariables.map().values().stream()
        .filter(b -> typing.contains(b.bounds().lower(), typing.any()))
        .map(b -> parseError(call, "Cannot infer actual type for type variable "
            + b.variable().q() + "."))
        .collect(toImmutableList());
  }
}
