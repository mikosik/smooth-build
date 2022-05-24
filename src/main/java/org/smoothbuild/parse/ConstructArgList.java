package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.nCopies;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.like.Param;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgN;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.parse.ast.DefaultArgN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ConstructArgList {
  public static Maybe<ImmutableList<ArgN>> constructArgList(CallN call, NList<Param> params) {
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
    var result = logBuffer.containsProblem() ? null : ImmutableList.copyOf(assignedArgs);
    return maybeValueAndLogs(result, logBuffer);
  }

  private static ImmutableList<ArgN> leadingPositionalArgs(CallN call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgN::declaresName))
        .collect(toImmutableList());
  }

  private static List<ArgN> assignedArgs(CallN call, NList<Param> params) {
    var args = call.args();
    var result = new ArrayList<ArgN>(nCopies(params.size(), null));
    for (int i = 0; i < args.size(); i++) {
      var arg = args.get(i);
      if (arg.declaresName()) {
        result.set(params.indexMap().get(arg.name()), arg);
      } else {
        result.set(i, arg);
      }
    }
    for (int i = 0; i < result.size(); i++) {
      if (result.get(i) == null) {
        var body = params.get(i).body();
        if (body.isPresent()) {
          result.set(i, new DefaultArgN(body.get(), call.loc()));
        }
      }
    }
    return result;
  }

  private static List<Log> findPositionalArgAfterNamedArgError(CallN call, NList<Param> params) {
    return call.args()
        .stream()
        .dropWhile(not(ArgN::declaresName))
        .dropWhile(ArgN::declaresName)
        .map(a -> parseError(a, messagePrefix(params)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgsError(
      CallN call, List<ArgN> positionalArgs, NList<Param> params) {
    if (params.size() < positionalArgs.size()) {
      return list(parseError(call, messagePrefix(params) + "Too many positional arguments."));
    }
    return list();
  }

  private static List<Log> findUnknownParamNameErrors(CallN call, NList<Param> params) {
    return call.args()
        .stream()
        .filter(ArgN::declaresName)
        .filter(a -> !params.containsName(a.name()))
        .map(a -> parseError(a, messagePrefix(params) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallN call, List<ArgN> positionalArgs, NList<Param> params) {
    Set<String> names = positionalArgNames(positionalArgs, params);
    return call.args()
        .stream()
        .filter(ArgN::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, messagePrefix(params) + a.q() + " is already assigned."))
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
      List<ArgN> assignedList, NList<Param> params) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i) == null)
        .mapToObj(i -> paramsMustBeSpecifiedError(call, i, params.get(i), params))
        .collect(toList());
  }

  private static Log paramsMustBeSpecifiedError(
      CallN call, int i, Param param, NList<Param> params) {
    String paramName = param.nameO().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return parseError(call, messagePrefix(params) + "Parameter " + paramName + " must be specified.");
  }

  private static String messagePrefix(NList<Param> params) {
    var paramsString = params.stream()
        .map(Param::typeAndName)
        .collect(joining(", "));
    return "In call to function with parameters `" + "(" + paramsString + ")`: ";
  }
}
