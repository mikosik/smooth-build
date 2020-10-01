package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.lang.base.type.InferTypeParameters.inferTypeParameters;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.mapM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Evaluable;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.GenericBasicType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class InferCallTypeAndParamAssignment {
  public static void inferCallTypeAndParamAssignment(CallNode call, Definitions imported,
      ImmutableMap<String, CallableNode> callables, Logger logger) {
    call.setType(empty());
    List<ItemSignature> parameters = parameters(call, imported, callables);
    ValueWithLogs<List<Assigned>> assigned = assigned(call, parameters);
    logger.log(assigned.logs());
    if (assigned.hasProblems() || !allAssignedHaveInferredType(assigned.value())) {
      return;
    }
    var typeParametersMap = typeParametersMap(call, logger, parameters, assigned.value());
    if (typeParametersMap == null) {
      return;
    }
    call.setAssignedArgs(mapM(assigned.value(), Assigned::expr));
    call.setType(callableResultType(call, imported, callables)
        .map(t -> t.mapTypeParameters(typeParametersMap)));
  }

  private static boolean allAssignedHaveInferredType(List<Assigned> assigned) {
    return assigned.stream().allMatch(a -> a.type().isPresent());
  }

  private static ValueWithLogs<List<Assigned>> assigned(
      CallNode call, List<ItemSignature> parameters) {
    var result = new ValueWithLogs<List<Assigned>>();
    var nameToIndex = nameToIndex(parameters);
    ImmutableList<ArgNode> positionalArguments = leadingPositionalArguments(call);

    result.log(findPositionalArgumentAfterNamedArgumentError(call));
    result.log(findTooManyPositionalArgumentsError(call, positionalArguments, parameters));
    result.log(findUnknownParameterNameErrors(call, nameToIndex));
    result.log(findDuplicateAssignmentErrors(call, positionalArguments, parameters));
    if (result.hasProblems()) {
      return result;
    }

    List<ArgNode> assignedArgs = assignedArgs(call, parameters, nameToIndex);
    result.log(findUnassignedParametersWithoutDefaultValuesErrors(call, assignedArgs, parameters));
    result.log(findIllegalTypeAssignmentErrors(call, assignedArgs, parameters));
    result.setValue(assignedList(parameters, assignedArgs));
    return result;
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

  private static List<ArgNode> assignedArgs(
      CallNode call, List<ItemSignature> parameters, Map<String, Integer> nameToIndex) {
    List<ArgNode> args = call.args();
    List<ArgNode> assignedList = asList(new ArgNode[parameters.size()]);
    for (int i = 0; i < args.size(); i++) {
      ArgNode arg = args.get(i);
      if (arg.declaresName()) {
        assignedList.set(nameToIndex.get(arg.name()), arg);
      } else {
        assignedList.set(i, arg);
      }
    }
    return assignedList;
  }

  private static List<Log> findPositionalArgumentAfterNamedArgumentError(CallNode call) {
    return call.args()
        .stream()
        .dropWhile(not(ArgNode::declaresName))
        .dropWhile(ArgNode::declaresName)
        .map(a -> parseError(a, inCallToPrefix(call)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgumentsError(
      CallNode call, List<ArgNode> positionalArguments, List<ItemSignature> parameters) {
    if (parameters.size() < positionalArguments.size()) {
      return List.of(parseError(call, inCallToPrefix(call) + "Too many positional arguments."));
    }
    return List.of();
  }

  private static List<Log> findUnknownParameterNameErrors(
      CallNode call, Map<String, Integer> nameToIndex) {
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !nameToIndex.containsKey(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallNode call, List<ArgNode> positionalArguments, List<ItemSignature> parameters) {
    var names = new HashSet<String>();
    parameters.stream()
        .limit(positionalArguments.size())
        .forEach(p -> names.add(p.name()));
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static List<Log> findUnassignedParametersWithoutDefaultValuesErrors(CallNode call,
      List<ArgNode> assignedList, List<ItemSignature> parameters) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i) == null)
        .mapToObj(parameters::get)
        .filter(p -> p.defaultValueType().isEmpty())
        .map(p -> parameterMustBeSpecifiedError(call, p))
        .collect(toList());
  }

  private static Log parameterMustBeSpecifiedError(CallNode call, ItemSignature p) {
    return parseError(call, inCallToPrefix(call) + "Parameter " + p.q() + " must be specified.");
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

  private static List<ItemSignature> parameters(
      CallNode call, Definitions imported, ImmutableMap<String, CallableNode> callables) {
    String name = call.calledName();
    Evaluable evaluable = imported.evaluables().get(name);
    if (evaluable != null) {
      return ((Callable) evaluable).parameterSignatures();
    }
    CallableNode node = callables.get(name);
    if (node != null) {
      return node.parameterSignatures();
    }
    throw new RuntimeException("Couldn't find `" + call.calledName() + "` function.");
  }

  private static Map<GenericBasicType, Type> typeParametersMap(CallNode call, Logger logger,
      List<ItemSignature> parameters, List<Assigned> assigned) {
    List<Type> genericTypes = new ArrayList<>();
    List<Type> actualTypes = new ArrayList<>();
    for (int i = 0; i < parameters.size(); i++) {
      if (parameters.get(i).type().hasGenericTypeParameters()) {
        genericTypes.add(parameters.get(i).type());
        actualTypes.add(assigned.get(i).type().get());
      }
    }
    if (actualTypes.contains(null)) {
      return null;
    }
    try {
      return inferTypeParameters(genericTypes, actualTypes);
    } catch (IllegalArgumentException e) {
      logger.log(
          parseError(call, "Cannot infer actual type(s) for generic parameter(s) in call to `"
              + call.calledName() + "`."));
      return null;
    }
  }

  private static Optional<Type> callableResultType(CallNode call, Definitions imported,
      ImmutableMap<String, CallableNode> callables) {
    String name = call.calledName();
    Callable callable = (Callable) imported.evaluables().get(name);
    if (callable != null) {
      return Optional.of(callable.resultType());
    }
    CallableNode node = callables.get(name);
    if (node != null) {
      return node.type();
    }
    throw new RuntimeException("Couldn't find `" + call.calledName() + "` function.");
  }

  private static Map<String, Integer> nameToIndex(List<ItemSignature> parameters) {
    return range(0, parameters.size())
        .boxed()
        .collect(toMap(i -> parameters.get(i).name(), i -> i));
  }

  private static String inCallToPrefix(CallNode call) {
    return "In call to `" + call.calledName() + "`: ";
  }

  private static ImmutableList<ArgNode> leadingPositionalArguments(CallNode call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgNode::declaresName))
        .collect(toImmutableList());
  }

  private static record Assigned(Optional<Type> type, ExprNode expr, Location location) {
    public Assigned(ArgNode arg) {
      this(arg.type(), arg.expr(), arg.location());
    }

    public Assigned(Optional<Type> type) {
      this(type, null, null);
    }
  }
}
