package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;

import com.google.common.collect.ImmutableList;

public class InferArgsToParamsAssignment {
  public static Maybe<List<ArgNode>> inferArgsToParamsAssignment(
      CallNode call, List<ItemSignature> parameters) {
    var logBuffer = new LogBuffer();
    var nameToIndex = nameToIndex(parameters);
    ImmutableList<ArgNode> positionalArguments = leadingPositionalArguments(call);

    logBuffer.logAll(findPositionalArgumentAfterNamedArgumentError(call));
    logBuffer.logAll(findTooManyPositionalArgumentsError(call, positionalArguments, parameters));
    logBuffer.logAll(findUnknownParameterNameErrors(call, nameToIndex));
    logBuffer.logAll(findDuplicateAssignmentErrors(call, positionalArguments, parameters));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    List<ArgNode> assignedArgs = assignedArgs(call, parameters, nameToIndex);
    logBuffer.logAll(findUnassignedParametersWithoutDefaultValuesErrors(
        call, assignedArgs, parameters));
    return maybeValueAndLogs(assignedArgs, logBuffer);
  }

  private static ImmutableList<ArgNode> leadingPositionalArguments(CallNode call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgNode::declaresName))
        .collect(toImmutableList());
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
        .forEach(p -> names.add(p.name().get()));
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
        .filter(p -> !p.hasDefaultValue())
        .map(p -> parameterMustBeSpecifiedError(call, p))
        .collect(toList());
  }

  private static Log parameterMustBeSpecifiedError(CallNode call, ItemSignature param) {
    return parseError(call, inCallToPrefix(call) + "Parameter `" + param.name().get() +
        "` must be specified.");
  }

  private static Map<String, Integer> nameToIndex(List<ItemSignature> parameters) {
    return range(0, parameters.size())
        .boxed()
        .collect(toMap(i -> parameters.get(i).name().get(), i -> i));
  }

  private static String inCallToPrefix(CallNode call) {
    return "In call to function with type " + call.called().type().get().q() + ": ";
  }
}
