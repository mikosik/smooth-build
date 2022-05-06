package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.join;
import static java.util.Collections.nCopies;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.impl.FuncTS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class InferArgsToParamsAssignment {
  public static Maybe<List<Optional<ArgNode>>> inferArgsToParamsAssignment(
      CallN call, NList<ItemSigS> params) {
    var logBuffer = new LogBuffer();
    var positionalArgs = leadingPositionalArgs(call);
    logBuffer.logAll(findPositionalArgAfterNamedArgError(call, params));
    logBuffer.logAll(findTooManyPositionalArgsError(call, positionalArgs, params));
    logBuffer.logAll(findUnknownParamNameErrors(call, params));
    logBuffer.logAll(findDuplicateAssignmentErrors(call, positionalArgs, params));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    var assignedArgs = assignedArgs(call, params);
    logBuffer.logAll(findUnassignedParamsWithoutDefaultArgsErrors(call, assignedArgs, params));
    var result = logBuffer.containsProblem() ? null : assignedArgs;
    return maybeValueAndLogs(result, logBuffer);
  }

  private static ImmutableList<ArgNode> leadingPositionalArgs(CallN call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgNode::declaresName))
        .collect(toImmutableList());
  }

  private static List<Optional<ArgNode>> assignedArgs(CallN call, NList<ItemSigS> params) {
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

  private static List<Log> findPositionalArgAfterNamedArgError(CallN call, List<ItemSigS> params) {
    return call.args()
        .stream()
        .dropWhile(not(ArgNode::declaresName))
        .dropWhile(ArgNode::declaresName)
        .map(a -> parseError(a, inCallToPrefix(call, params)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgsError(
      CallN call, List<ArgNode> positionalArgs, List<ItemSigS> params) {
    if (params.size() < positionalArgs.size()) {
      return list(parseError(
          call, inCallToPrefix(call, params) + "Too many positional arguments."));
    }
    return list();
  }

  private static List<Log> findUnknownParamNameErrors(CallN call, NList<ItemSigS> params) {
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !params.containsName(a.name()))
        .map(a -> parseError(a,
            inCallToPrefix(call, params) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallN call, List<ArgNode> positionalArgs, NList<ItemSigS> params) {
    Set<String> names = positionalArgNames(positionalArgs, params);
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call, params) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static Set<String> positionalArgNames(
      List<ArgNode> positionalArgs, NList<ItemSigS> params) {
    return params.stream()
        .limit(positionalArgs.size())
        .flatMap(p -> p.nameO().stream())
        .collect(toSet());
  }

  private static List<Log> findUnassignedParamsWithoutDefaultArgsErrors(CallN call,
      List<Optional<ArgNode>> assignedList, NList<ItemSigS> params) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isEmpty())
        .filter(i -> params.get(i).defaultValT().isEmpty())
        .mapToObj(i -> paramsMustBeSpecifiedError(call, i, params.get(i), params))
        .collect(toList());
  }

  private static Log paramsMustBeSpecifiedError(CallN call, int i, ItemSigS param,
      List<ItemSigS> params) {
    String paramName = param.nameO().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return parseError(call,
        inCallToPrefix(call, params) + "Parameter " + paramName + " must be specified.");
  }

  private static String inCallToPrefix(CallN call, List<ItemSigS> params) {
    String result = ((FuncTS) call.callable().type().get()).res().name();
    String paramsString = join(", ", map(params, ItemSigS::typeAndName));
    return "In call to function with type `" + result + "(" + paramsString + ")`: ";
  }
}
