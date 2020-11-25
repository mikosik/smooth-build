package org.smoothbuild.lang.parse;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.lang.base.type.InferTypeVariables.inferTypeVariables;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeVariable;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.ExprNode;

public class InferCallType {
  public static void inferCallType(CallNode call, Context context, Logger logger) {
    call.setType(empty());
    List<ItemSignature> parameters = context.parametersOf(call.calledName());
    List<ArgNode> assignedArgs = call.assignedArgs();
    List<Log> typeErrors = findIllegalTypeAssignmentErrors(call, assignedArgs, parameters);
    if (!typeErrors.isEmpty()) {
      logger.log(typeErrors);
      return;
    }
    List<Assigned> assigned = assignedList(parameters, assignedArgs);
    if (!allAssignedHaveInferredType(assigned)) {
      return;
    }
    var typeVariablesMap = typeVariablesMap(call, logger, parameters, assigned);
    if (typeVariablesMap == null) {
      return;
    }
    call.setType(context.resultTypeOf(call.calledName())
        .map(t -> t.mapTypeVariables(typeVariablesMap)));
  }

  private static List<Log> findIllegalTypeAssignmentErrors(
      CallNode call, List<ArgNode> assignedList, List<ItemSignature> parameters) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i) != null)
        .filter(i -> !isAssignable(parameters.get(i), assignedList.get(i)))
        .mapToObj(i -> illegalAssignmentError(call, parameters.get(i), assignedList.get(i)))
        .collect(toList());
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

  private static List<Assigned> assignedList(
      List<ItemSignature> parameters, List<ArgNode> assignedArgs) {
    List<Assigned> assigned = asList(new Assigned[parameters.size()]);
    for (int i = 0; i < assigned.size(); i++) {
      ArgNode arg = assignedArgs.get(i);
      if (arg == null) {
        assigned.set(i, new Assigned(parameters.get(i).defaultValueType()));
      } else {
        assigned.set(i, new Assigned(arg));
      }
    }
    return assigned;
  }

  private static boolean allAssignedHaveInferredType(List<Assigned> assigned) {
    return assigned.stream().allMatch(a -> a.type().isPresent());
  }

  private static Map<TypeVariable, Type> typeVariablesMap(CallNode call, Logger logger,
      List<ItemSignature> parameters, List<Assigned> assigned) {
    List<Type> parameterTypes = new ArrayList<>();
    List<Type> assignedTypes = new ArrayList<>();
    for (int i = 0; i < parameters.size(); i++) {
      if (parameters.get(i).type().isPolytype()) {
        parameterTypes.add(parameters.get(i).type());
        assignedTypes.add(assigned.get(i).type().get());
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

  public static record Assigned(Optional<Type> type, ExprNode expr, Location location) {
    public Assigned(ArgNode arg) {
      this(arg.type(), arg.expr(), arg.location());
    }

    public Assigned(Optional<Type> type) {
      this(type, null, null);
    }
  }
}
