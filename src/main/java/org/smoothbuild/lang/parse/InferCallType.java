package org.smoothbuild.lang.parse;

import static java.util.stream.IntStream.range;
import static org.smoothbuild.lang.base.type.InferTypeVariables.inferTypeVariables;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeVariable;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;

public class InferCallType {
  public static Maybe<Type> inferCallType(CallNode call, Optional<Type> resultType,
      List<ItemSignature> parameters) {
    Maybe<Type> result = new Maybe<>();
    List<ArgNode> assignedArgs = call.assignedArgs();
    findIllegalTypeAssignmentErrors(call, assignedArgs, parameters, result);
    if (result.hasProblems()) {
      return result;
    }
    List<Optional<Type>> assignedTypes = assignedTypes(parameters, assignedArgs);
    if (!allAssignedTypesAreInferred(assignedTypes)) {
      return result;
    }
    var typeVariablesMap = typeVariablesMap(call, result, parameters, assignedTypes);
    if (typeVariablesMap == null) {
      return result;
    }
    if (resultType.isPresent()) {
      result.setValue(resultType.get().mapTypeVariables(typeVariablesMap));
    }
    return result;
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
    return "In call to `" + call.calledName() + "`: ";
  }

  private static List<Optional<Type>> assignedTypes(
      List<ItemSignature> parameters, List<ArgNode> arguments) {
    List<Optional<Type>> assigned = new ArrayList<>();
    for (int i = 0; i < parameters.size(); i++) {
      ArgNode arg = arguments.get(i);
      if (arg == null) {
        assigned.add(parameters.get(i).defaultValueType());
      } else {
        assigned.add(arg.type());
      }
    }
    return assigned;
  }

  private static boolean allAssignedTypesAreInferred(List<Optional<Type>> assigned) {
    return assigned.stream().allMatch(Optional::isPresent);
  }

  private static Map<TypeVariable, Type> typeVariablesMap(CallNode call, Logger logger,
      List<ItemSignature> parameters, List<Optional<Type>> assigned) {
    List<Type> parameterTypes = new ArrayList<>();
    List<Type> assignedTypes = new ArrayList<>();
    for (int i = 0; i < parameters.size(); i++) {
      if (parameters.get(i).type().isPolytype()) {
        parameterTypes.add(parameters.get(i).type());
        assignedTypes.add(assigned.get(i).get());
      }
    }
    try {
      return inferTypeVariables(parameterTypes, assignedTypes);
    } catch (IllegalArgumentException e) {
      logger.log(
          parseError(call, "Cannot infer actual type(s) for parameter(s) in call to `"
              + call.calledName() + "`."));
      return null;
    }
  }
}
