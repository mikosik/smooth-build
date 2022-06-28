package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.nCopies;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.like.Param;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgP;
import org.smoothbuild.parse.ast.CallP;
import org.smoothbuild.parse.ast.DefaultArgP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ConstructArgList {
  public static Maybe<ImmutableList<ArgP>> constructArgList(CallP call, NList<Param> params) {
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

  private static ImmutableList<ArgP> leadingPositionalArgs(CallP call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgP::declaresName))
        .collect(toImmutableList());
  }

  private static List<ArgP> assignedArgs(CallP call, NList<Param> params) {
    var args = call.args();
    var result = new ArrayList<ArgP>(nCopies(params.size(), null));
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
          result.set(i, new DefaultArgP(body.get(), call.loc()));
        }
      }
    }
    return result;
  }

  private static List<Log> findPositionalArgAfterNamedArgError(CallP call, NList<Param> params) {
    return call.args()
        .stream()
        .dropWhile(not(ArgP::declaresName))
        .dropWhile(ArgP::declaresName)
        .map(a -> parseError(a, messagePrefix(params)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgsError(
      CallP call, List<ArgP> positionalArgs, NList<Param> params) {
    if (params.size() < positionalArgs.size()) {
      return list(parseError(call, messagePrefix(params) + "Too many positional arguments."));
    }
    return list();
  }

  private static List<Log> findUnknownParamNameErrors(CallP call, NList<Param> params) {
    return call.args()
        .stream()
        .filter(ArgP::declaresName)
        .filter(a -> !params.containsName(a.name()))
        .map(a -> parseError(a, messagePrefix(params) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallP call, List<ArgP> positionalArgs, NList<Param> params) {
    Set<String> names = positionalArgNames(positionalArgs, params);
    return call.args()
        .stream()
        .filter(ArgP::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, messagePrefix(params) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static Set<String> positionalArgNames(
      List<ArgP> positionalArgs, NList<Param> params) {
    return params.stream()
        .limit(positionalArgs.size())
        .flatMap(p -> p.nameO().stream())
        .collect(toSet());
  }

  private static List<Log> findUnassignedParamsWithoutDefaultArgsErrors(CallP call,
      List<ArgP> assignedList, NList<Param> params) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i) == null)
        .mapToObj(i -> paramsMustBeSpecifiedError(call, i, params.get(i), params))
        .collect(toList());
  }

  private static Log paramsMustBeSpecifiedError(
      CallP call, int i, Param param, NList<Param> params) {
    String paramName = param.nameO().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return parseError(call, messagePrefix(params) + "Parameter " + paramName + " must be specified.");
  }

  private static String messagePrefix(NList<Param> params) {
    var paramsString = toCommaSeparatedString(params, Param::typeAndName);
    return "In call to function with parameters `" + "(" + paramsString + ")`: ";
  }
}
