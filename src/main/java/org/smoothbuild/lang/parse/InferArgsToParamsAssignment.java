package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.join;
import static java.util.Collections.nCopies;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public class InferArgsToParamsAssignment {
  public static Maybe<List<Optional<ArgNode>>> inferArgsToParamsAssignment(
      CallNode call, List<ItemSignature> parameters) {
    var logBuffer = new LogBuffer();
    var namedTypes = namedList(map(parameters, ItemSignature::toNamedType));
    ImmutableList<ArgNode> positionalArguments = leadingPositionalArguments(call);

    logBuffer.logAll(findPositionalArgumentAfterNamedArgumentError(call, parameters));
    logBuffer.logAll(findTooManyPositionalArgumentsError(call, positionalArguments, parameters));
    logBuffer.logAll(findUnknownParameterNameErrors(call, namedTypes, parameters));
    logBuffer.logAll(findDuplicateAssignmentErrors(call, positionalArguments, parameters));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    List<Optional<ArgNode>> assignedArgs = assignedArgs(call, parameters, namedTypes);
    logBuffer.logAll(
        findUnassignedParametersWithoutDefaultArgumentsErrors(call, assignedArgs, parameters));
    return maybeValueAndLogs(assignedArgs, logBuffer);
  }

  private static ImmutableList<ArgNode> leadingPositionalArguments(CallNode call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgNode::declaresName))
        .collect(toImmutableList());
  }

  private static List<Optional<ArgNode>> assignedArgs(
      CallNode call, List<ItemSignature> parameters, NamedList<TypeS> namedTypes) {
    List<ArgNode> args = call.args();
    List<Optional<ArgNode>> assignedList =
        new ArrayList<>(nCopies(parameters.size(), Optional.empty()));
    for (int i = 0; i < args.size(); i++) {
      ArgNode arg = args.get(i);
      if (arg.declaresName()) {
        assignedList.set(namedTypes.indexMap().get(arg.name()), Optional.of(arg));
      } else {
        assignedList.set(i, Optional.of(arg));
      }
    }
    return assignedList;
  }

  private static List<Log> findPositionalArgumentAfterNamedArgumentError(
      CallNode call, List<ItemSignature> parameters) {
    return call.args()
        .stream()
        .dropWhile(not(ArgNode::declaresName))
        .dropWhile(ArgNode::declaresName)
        .map(a -> parseError(a, inCallToPrefix(call, parameters)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgumentsError(
      CallNode call, List<ArgNode> positionalArguments, List<ItemSignature> parameters) {
    if (parameters.size() < positionalArguments.size()) {
      return list(parseError(
          call, inCallToPrefix(call, parameters) + "Too many positional arguments."));
    }
    return list();
  }

  private static List<Log> findUnknownParameterNameErrors(
      CallNode call, NamedList<TypeS> namedTypes, List<ItemSignature> parameters) {
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !namedTypes.contains(a.name()))
        .map(a -> parseError(a,
            inCallToPrefix(call, parameters) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallNode call, List<ArgNode> positionalArguments, List<ItemSignature> parameters) {
    Set<String> names = positionalArgumentNames(positionalArguments, parameters);
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call, parameters) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static Set<String> positionalArgumentNames(List<ArgNode> positionalArguments,
      List<ItemSignature> parameters) {
    return parameters.stream()
        .limit(positionalArguments.size())
        .flatMap(p -> p.name().stream())
        .collect(toSet());
  }

  private static List<Log> findUnassignedParametersWithoutDefaultArgumentsErrors(CallNode call,
      List<Optional<ArgNode>> assignedList, List<ItemSignature> parameters) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isEmpty())
        .filter(i -> parameters.get(i).defaultValueType().isEmpty())
        .mapToObj(i -> parameterMustBeSpecifiedError(call, i, parameters.get(i), parameters))
        .collect(toList());
  }

  private static Log parameterMustBeSpecifiedError(CallNode call, int i, ItemSignature param,
      List<ItemSignature> parameters) {
    String paramName = param.name().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return parseError(call,
        inCallToPrefix(call, parameters) + "Parameter " + paramName + " must be specified.");
  }

  private static String inCallToPrefix(CallNode call, List<ItemSignature> parameters) {
    String result = ((FunctionType) call.function().type().get()).result().name();
    String params = join(", ", map(parameters, ItemSignature::typeAndName));
    return "In call to function with type `" + result + "(" + params + ")`: ";
  }
}
