package org.smoothbuild.lang.parse;

import static java.util.stream.IntStream.range;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Type.inferVariableBounds;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;

public class InferCallType {
  public static Maybe<Type> inferCallType(CallNode call, Type resultType,
      List<ItemSignature> parameters) {
    var logBuffer = new LogBuffer();
    List<ArgNode> assignedArgs = call.assignedArgs();
    findIllegalTypeAssignmentErrors(call, assignedArgs, parameters, logBuffer);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    List<Optional<Type>> assignedTypes = assignedTypes(parameters, assignedArgs);
    if (allAssignedTypesAreInferred(assignedTypes)) {
      var variableToBounds = inferVariableBounds(
          map(parameters, ItemSignature::type),
          map(assignedTypes, Optional::get),
          LOWER);
      return maybeValueAndLogs(resultType.mapVariables(variableToBounds, LOWER), logBuffer);
    }
    return maybeLogs(logBuffer);
  }

  private static void findIllegalTypeAssignmentErrors(
      CallNode call, List<ArgNode> assignedList, List<ItemSignature> parameters, Logger logger) {
    range(0, assignedList.size())
        .filter(i -> assignedList.get(i) != null)
        .filter(i -> !isAssignable(parameters.get(i), assignedList.get(i)))
        .mapToObj(i -> illegalAssignmentError(call, parameters.get(i), assignedList.get(i)))
        .forEach(logger::log);
  }

  private static boolean isAssignable(ItemSignature parameter, ArgNode arg) {
    return parameter.type().isParamAssignableFrom(arg.type().get());
  }

  private static Log illegalAssignmentError(CallNode call, ItemSignature parameter, ArgNode arg) {
    return parseError(arg.location(), inCallToPrefix(call)
        + "Cannot assign argument of type " + arg.type().get().q() + " to parameter "
        + parameter.q() + " of type " + parameter.type().q() + ".");
  }

  private static String inCallToPrefix(CallNode call) {
    return "In call to function with type " + call.called().type().get().q() + ": ";
  }

  private static List<Optional<Type>> assignedTypes(
      List<ItemSignature> parameters, List<ArgNode> arguments) {
    List<Optional<Type>> assigned = new ArrayList<>();
    for (int i = 0; i < parameters.size(); i++) {
      ArgNode arg = arguments.get(i);
      if (arg == null) {
        assigned.add(parameters.get(i).defaultValueType());
      } else {
        assigned.add(arg.type().map(Type::strip));
      }
    }
    return assigned;
  }

  private static boolean allAssignedTypesAreInferred(List<Optional<Type>> assigned) {
    return assigned.stream().allMatch(Optional::isPresent);
  }
}
