package org.smoothbuild.lang.parse;

import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.lang.base.type.InferTypeVariables.inferTypeVariables;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeVariable;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.ExprNode;

public class InferCallType {
  public static ValueWithLogs<Type> inferCallType(CallNode call, Context context) {
    ValueWithLogs<Type> result = new ValueWithLogs<>();
    List<ItemSignature> parameters = context.parametersOf(call.calledName());
    List<ArgNode> assignedArgs = call.assignedArgs();
    findIllegalTypeAssignmentErrors(call, assignedArgs, parameters, result);
    if (result.hasProblems()) {
      return result;
    }
    List<Assigned> assigned = assignedList(parameters, assignedArgs);
    if (!allAssignedHaveInferredType(assigned)) {
      return result;
    }
    var typeVariablesMap = typeVariablesMap(call, result, parameters, assigned);
    if (typeVariablesMap == null) {
      return result;
    }
    Optional<Type> type = context.resultTypeOf(call.calledName());
    if (type.isPresent()) {
      result.setValue(type.get().mapTypeVariables(typeVariablesMap));
    }
    return result;
  }

  private static void findIllegalTypeAssignmentErrors(
      CallNode call, List<ArgNode> assignedList, List<ItemSignature> parameters,
      Logger result) {
    range(0, assignedList.size())
        .filter(i -> assignedList.get(i) != null)
        .filter(i -> !isAssignable(parameters.get(i), assignedList.get(i)))
        .mapToObj(i -> illegalAssignmentError(call, parameters.get(i), assignedList.get(i)))
        .forEach(result::log);
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
