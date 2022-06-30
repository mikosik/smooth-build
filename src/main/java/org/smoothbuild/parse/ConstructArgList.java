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

import org.smoothbuild.lang.like.common.ParamC;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.ArgP;
import org.smoothbuild.parse.ast.CallP;
import org.smoothbuild.parse.ast.DefaultArgP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ConstructArgList {
  public static Maybe<ImmutableList<ArgP>> constructArgList(CallP call, NList<ParamC> paramCs) {
    var logBuffer = new LogBuffer();
    var positionalArgs = leadingPositionalArgs(call);
    logBuffer.logAll(findPositionalArgAfterNamedArgError(call, paramCs));
    logBuffer.logAll(findTooManyPositionalArgsError(call, positionalArgs, paramCs));
    logBuffer.logAll(findUnknownParamNameErrors(call, paramCs));
    logBuffer.logAll(findDuplicateAssignmentErrors(call, positionalArgs, paramCs));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    var assignedArgs = assignedArgs(call, paramCs);
    logBuffer.logAll(findUnassignedParamsWithoutDefaultArgsErrors(call, assignedArgs, paramCs));
    var result = logBuffer.containsProblem() ? null : ImmutableList.copyOf(assignedArgs);
    return maybeValueAndLogs(result, logBuffer);
  }

  private static ImmutableList<ArgP> leadingPositionalArgs(CallP call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgP::declaresName))
        .collect(toImmutableList());
  }

  private static List<ArgP> assignedArgs(CallP call, NList<ParamC> paramCs) {
    var args = call.args();
    var result = new ArrayList<ArgP>(nCopies(paramCs.size(), null));
    for (int i = 0; i < args.size(); i++) {
      var arg = args.get(i);
      if (arg.declaresName()) {
        result.set(paramCs.indexMap().get(arg.name()), arg);
      } else {
        result.set(i, arg);
      }
    }
    for (int i = 0; i < result.size(); i++) {
      if (result.get(i) == null) {
        var body = paramCs.get(i).body();
        if (body.isPresent()) {
          result.set(i, new DefaultArgP(body.get(), call.loc()));
        }
      }
    }
    return result;
  }

  private static List<Log> findPositionalArgAfterNamedArgError(CallP call, NList<ParamC> paramCs) {
    return call.args()
        .stream()
        .dropWhile(not(ArgP::declaresName))
        .dropWhile(ArgP::declaresName)
        .map(a -> parseError(a, messagePrefix(paramCs)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgsError(
      CallP call, List<ArgP> positionalArgs, NList<ParamC> paramCs) {
    if (paramCs.size() < positionalArgs.size()) {
      return list(parseError(call, messagePrefix(paramCs) + "Too many positional arguments."));
    }
    return list();
  }

  private static List<Log> findUnknownParamNameErrors(CallP call, NList<ParamC> paramCs) {
    return call.args()
        .stream()
        .filter(ArgP::declaresName)
        .filter(a -> !paramCs.containsName(a.name()))
        .map(a -> parseError(a, messagePrefix(paramCs) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallP call, List<ArgP> positionalArgs, NList<ParamC> paramCs) {
    Set<String> names = positionalArgNames(positionalArgs, paramCs);
    return call.args()
        .stream()
        .filter(ArgP::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, messagePrefix(paramCs) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static Set<String> positionalArgNames(
      List<ArgP> positionalArgs, NList<ParamC> paramCs) {
    return paramCs.stream()
        .limit(positionalArgs.size())
        .flatMap(p -> p.nameO().stream())
        .collect(toSet());
  }

  private static List<Log> findUnassignedParamsWithoutDefaultArgsErrors(CallP call,
      List<ArgP> assignedList, NList<ParamC> paramCs) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i) == null)
        .mapToObj(i -> paramsMustBeSpecifiedError(call, i, paramCs.get(i), paramCs))
        .collect(toList());
  }

  private static Log paramsMustBeSpecifiedError(
      CallP call, int i, ParamC paramC, NList<ParamC> paramCs) {
    String paramName = paramC.nameO().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return parseError(call, messagePrefix(paramCs) + "Parameter " + paramName + " must be specified.");
  }

  private static String messagePrefix(NList<ParamC> paramCs) {
    var paramsString = toCommaSeparatedString(paramCs, ParamC::typeAndName);
    return "In call to function with parameters `" + "(" + paramsString + ")`: ";
  }
}
