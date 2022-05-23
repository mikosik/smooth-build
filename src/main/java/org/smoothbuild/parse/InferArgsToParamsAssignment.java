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

import org.smoothbuild.lang.like.Param;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgN;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class InferArgsToParamsAssignment {
  public static Maybe<List<Optional<ArgN>>> inferArgsToParamsAssignment(
      CallN call, NList<Param> params) {
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

  private static ImmutableList<ArgN> leadingPositionalArgs(CallN call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgN::declaresName))
        .collect(toImmutableList());
  }

  private static List<Optional<ArgN>> assignedArgs(CallN call, NList<Param> params) {
    List<ArgN> args = call.args();
    List<Optional<ArgN>> assignedList =
        new ArrayList<>(nCopies(params.size(), Optional.empty()));
    for (int i = 0; i < args.size(); i++) {
      ArgN arg = args.get(i);
      if (arg.declaresName()) {
        assignedList.set(params.indexMap().get(arg.name()), Optional.of(arg));
      } else {
        assignedList.set(i, Optional.of(arg));
      }
    }
    return assignedList;
  }

  private static List<Log> findPositionalArgAfterNamedArgError(CallN call, NList<Param> params) {
    return call.args()
        .stream()
        .dropWhile(not(ArgN::declaresName))
        .dropWhile(ArgN::declaresName)
        .map(a -> parseError(a, inCallToPrefix(call, params)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgsError(
      CallN call, List<ArgN> positionalArgs, NList<Param> params) {
    if (params.size() < positionalArgs.size()) {
      return list(parseError(
          call, inCallToPrefix(call, params) + "Too many positional arguments."));
    }
    return list();
  }

  private static List<Log> findUnknownParamNameErrors(CallN call, NList<Param> params) {
    return call.args()
        .stream()
        .filter(ArgN::declaresName)
        .filter(a -> !params.containsName(a.name()))
        .map(a -> parseError(a,
            inCallToPrefix(call, params) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallN call, List<ArgN> positionalArgs, NList<Param> params) {
    Set<String> names = positionalArgNames(positionalArgs, params);
    return call.args()
        .stream()
        .filter(ArgN::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call, params) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static Set<String> positionalArgNames(
      List<ArgN> positionalArgs, NList<Param> params) {
    return params.stream()
        .limit(positionalArgs.size())
        .flatMap(p -> p.nameO().stream())
        .collect(toSet());
  }

  private static List<Log> findUnassignedParamsWithoutDefaultArgsErrors(CallN call,
      List<Optional<ArgN>> assignedList, NList<Param> params) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isEmpty())
        .filter(i -> params.get(i).body().isEmpty())
        .mapToObj(i -> paramsMustBeSpecifiedError(call, i, params.get(i), params))
        .collect(toList());
  }

  private static Log paramsMustBeSpecifiedError(
      CallN call, int i, Param param, NList<Param> params) {
    String paramName = param.nameO().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return parseError(call,
        inCallToPrefix(call, params) + "Parameter " + paramName + " must be specified.");
  }

  private static String inCallToPrefix(CallN call, NList<Param> params) {
    String result = ((FuncTS) call.callable().type().get()).res().name();
    String paramsString = join(", ", map(params, Param::typeAndName));
    return "In call to function with type `" + result + "(" + paramsString + ")`: ";
  }
}
