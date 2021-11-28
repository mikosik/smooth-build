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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class InferArgsToParamsAssignment {
  public static Maybe<List<Optional<ArgNode>>> inferArgsToParamsAssignment(
      CallNode call, NList<ItemSignature> params) {
    var logBuffer = new LogBuffer();
    ImmutableList<ArgNode> positionalArguments = leadingPositionalArguments(call);
    logBuffer.logAll(findPositionalArgumentAfterNamedArgumentError(call, params));
    logBuffer.logAll(findTooManyPositionalArgumentsError(call, positionalArguments, params));
    logBuffer.logAll(findUnknownParamNameErrors(call, params));
    logBuffer.logAll(findDuplicateAssignmentErrors(call, positionalArguments, params));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    List<Optional<ArgNode>> assignedArgs = assignedArgs(call, params);
    logBuffer.logAll(
        findUnassignedParamsWithoutDefaultArgsErrors(call, assignedArgs, params));
    return maybeValueAndLogs(assignedArgs, logBuffer);
  }

  private static ImmutableList<ArgNode> leadingPositionalArguments(CallNode call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgNode::declaresName))
        .collect(toImmutableList());
  }

  private static List<Optional<ArgNode>> assignedArgs(
      CallNode call, NList<ItemSignature> params) {
    List<ArgNode> args = call.args();
    List<Optional<ArgNode>> assignedList =
        new ArrayList<>(nCopies(params.size(), Optional.empty()));
    for (int i = 0; i < args.size(); i++) {
      ArgNode arg = args.get(i);
      if (arg.declaresName()) {
        assignedList.set(params.indexMap().get(arg.name()), Optional.of(arg));
      } else {
        assignedList.set(i, Optional.of(arg));
      }
    }
    return assignedList;
  }

  private static List<Log> findPositionalArgumentAfterNamedArgumentError(
      CallNode call, List<ItemSignature> params) {
    return call.args()
        .stream()
        .dropWhile(not(ArgNode::declaresName))
        .dropWhile(ArgNode::declaresName)
        .map(a -> parseError(a, inCallToPrefix(call, params)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgumentsError(
      CallNode call, List<ArgNode> positionalArguments, List<ItemSignature> params) {
    if (params.size() < positionalArguments.size()) {
      return list(parseError(
          call, inCallToPrefix(call, params) + "Too many positional arguments."));
    }
    return list();
  }

  private static List<Log> findUnknownParamNameErrors(
      CallNode call, NList<ItemSignature> params) {
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !params.containsName(a.name()))
        .map(a -> parseError(a,
            inCallToPrefix(call, params) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallNode call, List<ArgNode> positionalArguments, NList<ItemSignature> params) {
    Set<String> names = positionalArgumentNames(positionalArguments, params);
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call, params) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static Set<String> positionalArgumentNames(List<ArgNode> positionalArguments,
      NList<ItemSignature> params) {
    return params.stream()
        .limit(positionalArguments.size())
        .flatMap(p -> p.nameO().stream())
        .collect(toSet());
  }

  private static List<Log> findUnassignedParamsWithoutDefaultArgsErrors(CallNode call,
      List<Optional<ArgNode>> assignedList, NList<ItemSignature> params) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isEmpty())
        .filter(i -> params.get(i).defaultValueType().isEmpty())
        .mapToObj(i -> paramsMustBeSpecifiedError(call, i, params.get(i), params))
        .collect(toList());
  }

  private static Log paramsMustBeSpecifiedError(CallNode call, int i, ItemSignature param,
      List<ItemSignature> params) {
    String paramName = param.nameO().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return parseError(call,
        inCallToPrefix(call, params) + "Parameter " + paramName + " must be specified.");
  }

  private static String inCallToPrefix(CallNode call, List<ItemSignature> params) {
    String result = ((FunctionType) call.function().type().get()).result().name();
    String paramsString = join(", ", map(params, ItemSignature::typeAndName));
    return "In call to function with type `" + result + "(" + paramsString + ")`: ";
  }
}
